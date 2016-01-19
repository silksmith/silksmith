package io.silksmith.platform;

import org.gradle.platform.base.component.BaseComponentSpec;

public class DefaultSilksmithWebAppSpec extends BaseComponentSpec implements SilksmithWebAppSpec {

	@Override
	public String getDisplayName() {
		return "silksmith web app spec";
	}
	@Override
	public void targetPlatform(String arg0) {
		throw new RuntimeException("TARGET PLATFORM NOT YET SUPPORTED");
		
	}

	
}
