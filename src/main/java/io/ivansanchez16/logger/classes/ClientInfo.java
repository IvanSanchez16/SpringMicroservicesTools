package io.ivansanchez16.logger.classes;

import java.util.UUID;

public record ClientInfo(
        String originAddress,
        String originHost,
        UUID transactionUUID
) { }
