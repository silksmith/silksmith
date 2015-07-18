package io.silksmith.development.server.css

import io.silksmith.css.sass.SassRunner

public interface SassRunnerProvider {
	SassRunner getSassRunner()
}