#!/usr/bin/env python3
"""Import the user's MySongbooks Nyimbo cia Agendi catalog for offline use."""
import json
import re
import time
import urllib.request
from concurrent.futures import ThreadPoolExecutor, as_completed
from html.parser import HTMLParser
from pathlib import Path
from bs4 import BeautifulSoup

ROOT = Path(__file__).resolve().parents[1]
CATALOG_URL = "https://www.mysongbooks.scaptedesigns.com/library/nyimbo-cia-agendi"
USER_AGENT = "NyimboCiaAgendiCatalogImporter/1.0"

class CatalogLinks(HTMLParser):
    def __init__(self):
        super().__init__(convert_charrefs=True)
        self.anchor = None
        self.entries = []

    def handle_starttag(self, tag, attrs):
        if tag == "a":
            self.anchor = {"href": dict(attrs).get("href", ""), "parts": []}

    def handle_data(self, data):
        if self.anchor is not None:
            self.anchor["parts"].append(data)

    def handle_endtag(self, tag):
        if tag == "a" and self.anchor is not None:
            text = " ".join("".join(self.anchor["parts"]).split())
            match = re.match(r"^(\d+)\.\s+(.+?)\s+Nyimbo cia Agendi$", text, re.I)
            if match and "/library/nyimbo-cia-agendi/" in self.anchor["href"]:
                self.entries.append({
                    "source_order": int(match.group(1)),
                    "title": match.group(2),
                    "source_url": self.anchor["href"],
                })
            self.anchor = None

def fetch(url):
    last_error = None
    for attempt in range(3):
        try:
            request = urllib.request.Request(url, headers={"User-Agent": USER_AGENT})
            with urllib.request.urlopen(request, timeout=30) as response:
                return response.read().decode("utf-8", "replace")
        except Exception as exc:
            last_error = exc
            if attempt < 2:
                time.sleep(1 + attempt)
    raise RuntimeError(f"Unable to fetch {url}: {last_error}")

def extract_song(url):
    soup = BeautifulSoup(fetch(url), "html.parser")
    card = soup.select_one("div.mb-3.rounded-md.bg-white")
    if card is None or card.find("h1") is None:
        raise RuntimeError(f"Song title/card not found at {url}")
    title = card.find("h1").get_text(" ", strip=True)
    lyrics_element = card.find("p")
    if lyrics_element is None:
        raise RuntimeError(f"Lyrics block not found at {url}")
    lyrics = lyrics_element.get_text("\n", strip=True).replace("\r", "")
    lyrics = re.sub(r"\n{3,}", "\n\n", lyrics).strip()
    if not lyrics:
        raise RuntimeError(f"Lyrics block is empty at {url}")
    return {"page_title": title, "lyrics": lyrics}

def main():
    parser = CatalogLinks()
    parser.feed(fetch(CATALOG_URL))
    rows = sorted(parser.entries, key=lambda row: row["source_order"])
    if len(rows) != 299:
        raise SystemExit(f"Expected 299 source entries, found {len(rows)}")
    unique_urls = sorted({row["source_url"] for row in rows})
    collected = {}
    failures = []
    with ThreadPoolExecutor(max_workers=5) as pool:
        futures = {pool.submit(extract_song, url): url for url in unique_urls}
        for future in as_completed(futures):
            url = futures[future]
            try:
                collected[url] = future.result()
            except Exception as exc:
                failures.append((url, str(exc)))
    if failures:
        raise SystemExit("Failed song pages:\n" + "\n".join(f"{url}: {error}" for url, error in failures))

    entries = []
    for row in rows:
        source = collected[row["source_url"]]
        entries.append({
            **row,
            "lyrics": source["lyrics"],
            "music_key": None,
            "keyboard_score_url": None,
            "source_page_title": source["page_title"],
        })
    payload = {
        "collection": "Nyimbo cia Agendi",
        "source_catalog_url": CATALOG_URL,
        "source_note": "Source directory order is not an official printed hymn number. The source pages supplied titles and lyrics; key and keyboard-score fields were not present.",
        "entry_count": len(entries),
        "entries": entries,
    }
    output = ROOT / "data" / "mysongbooks_nyimbo_cia_agendi_lyrics.json"
    output.write_text(json.dumps(payload, ensure_ascii=False, indent=2) + "\n", encoding="utf-8")
    print(f"Imported {len(entries)} directory entries from {len(unique_urls)} unique song pages.")
    print(f"Unique title strings: {len({row['title'].casefold() for row in rows})}; lyric characters: {sum(len(row['lyrics']) for row in entries)}.")
    print(f"JSON: {output}")

if __name__ == "__main__":
    main()
