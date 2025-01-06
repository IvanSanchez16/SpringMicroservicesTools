package io.ivansanchez16.logger.classes;

import java.util.List;

public record Event(
        String header,
        List<String> rows
) {}
