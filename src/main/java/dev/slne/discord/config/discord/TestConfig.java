package dev.slne.discord.config.discord;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

/**
 * The type Test config.
 */
@ConfigSerializable
public class TestConfig {

	private TestConfig2 testConfig2;

	/**
	 * Instantiates a new Test config.
	 */
	public TestConfig() {
	}
	
	/**
	 * Instantiates a new Test config.
	 *
	 * @param testConfig2 the test config 2
	 */
	public TestConfig(TestConfig2 testConfig2) {
		this.testConfig2 = testConfig2;
	}

	/**
	 * Gets test config 2.
	 *
	 * @return the test config 2
	 */
	public TestConfig2 getTestConfig2() {
		return testConfig2;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
}
