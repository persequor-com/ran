package io.ran.testclasses;

import io.ran.PrimaryKey;

import java.util.UUID;

public class WithBinaryField {
	@PrimaryKey
	private UUID uuid;
	private byte[] bytes;

	public UUID getUuid() {
		return uuid;
	}

	public void setUuid(UUID uuid) {
		this.uuid = uuid;
	}

	public byte[] getBytes() {
		return bytes;
	}

	public void setBytes(byte[] bytes) {
		this.bytes = bytes;
	}
}
