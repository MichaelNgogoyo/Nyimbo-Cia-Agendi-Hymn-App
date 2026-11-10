package org.nyimbo.agendi.config;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record NotificationRequest(@NotBlank @Size(max=120) String title, @NotBlank @Size(max=1000) String body,
                                  @Size(max=100) String eventId) {}
