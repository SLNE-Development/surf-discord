package dev.slne.discord.config.discord;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;

@ConfigSerializable
public class TestConfig2 {

	private String botToken;

	public TestConfig2() {
	}

	public TestConfig2(String botToken) {
		this.botToken = botToken;
	}

	public String getBotToken() {
		return botToken;
	}

	@Override
	public String toString() {
		return "TestConfig2{" +
			   "botToken='" + botToken + '\'' +
			   '}';
	}
}
