/* Copyright 2021 PSQR
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package io.ran;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Mapper(dbType = TestDb.class)
public class TestClass {
	@PrimaryKey
	private String id;
	@Key(name = "muh")
	private UUID otherId;
	private int integer;
	private byte aByte;
	private short aShort;
	private long aLong;
	private float aFloat;
	private double aDouble;
	private char aChar;
	private boolean aBoolean;
	private Integer integerObject;
	private Byte aByteObject;
	private Short aShortObject;
	private Long aLongObject;
	private Float aFloatObject;
	private Double aDoubleObject;
	private Character aCharObject;
	private Boolean aBooleanObject;
	@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
	private Double crashRating;
	private LocalDateTime localDateTime;

	private List<String> aList = new ArrayList<>();
	@Relation()
	private Other other;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public UUID getOtherId() {
		return otherId;
	}

	public void setOtherId(UUID otherId) {
		this.otherId = otherId;
	}


	public Other getOther() {
		return other;
	}

	public void setOther(Other other) {
		this.otherId = other.getId();
		this.other = other;
	}

	public Double getCrashRating() {
		return crashRating;
	}

	public void setCrashRating(Double crashRating) {
		this.crashRating = crashRating;
	}

	public int getInteger() {
		return integer;
	}

	public void setInteger(int integer) {
		this.integer = integer;
	}

	public byte getaByte() {
		return aByte;
	}

	public void setaByte(byte aByte) {
		this.aByte = aByte;
	}

	public short getaShort() {
		return aShort;
	}

	public void setaShort(short aShort) {
		this.aShort = aShort;
	}

	public long getaLong() {
		return aLong;
	}

	public void setaLong(long aLong) {
		this.aLong = aLong;
	}

	public float getaFloat() {
		return aFloat;
	}

	public void setaFloat(float aFloat) {
		this.aFloat = aFloat;
	}

	public double getaDouble() {
		return aDouble;
	}

	public void setaDouble(double aDouble) {
		this.aDouble = aDouble;
	}

	public char getaChar() {
		return aChar;
	}

	public void setaChar(char aChar) {
		this.aChar = aChar;
	}

	public boolean isaBoolean() {
		return aBoolean;
	}

	public void setaBoolean(boolean aBoolean) {
		this.aBoolean = aBoolean;
	}

	public Integer getIntegerObject() {
		return integerObject;
	}

	public void setIntegerObject(Integer integerObject) {
		this.integerObject = integerObject;
	}

	public Byte getaByteObject() {
		return aByteObject;
	}

	public void setaByteObject(Byte aByteObject) {
		this.aByteObject = aByteObject;
	}

	public Short getaShortObject() {
		return aShortObject;
	}

	public void setaShortObject(Short aShortObject) {
		this.aShortObject = aShortObject;
	}

	public Long getaLongObject() {
		return aLongObject;
	}

	public void setaLongObject(Long aLongObject) {
		this.aLongObject = aLongObject;
	}

	public Float getaFloatObject() {
		return aFloatObject;
	}

	public void setaFloatObject(Float aFloatObject) {
		this.aFloatObject = aFloatObject;
	}

	public Double getaDoubleObject() {
		return aDoubleObject;
	}

	public void setaDoubleObject(Double aDoubleObject) {
		this.aDoubleObject = aDoubleObject;
	}

	public Character getaCharObject() {
		return aCharObject;
	}

	public void setaCharObject(Character aCharObject) {
		this.aCharObject = aCharObject;
	}

	public Boolean getaBooleanObject() {
		return aBooleanObject;
	}

	public void setaBooleanObject(Boolean aBooleanObject) {
		this.aBooleanObject = aBooleanObject;
	}

	public List<String> getaList() {
		return aList;
	}

	public void setaList(List<String> aList) {
		this.aList = aList;
	}

	public LocalDateTime getLocalDateTime() {
		return localDateTime;
	}

	public void setLocalDateTime(LocalDateTime localDateTime) {
		this.localDateTime = localDateTime;
	}
}
