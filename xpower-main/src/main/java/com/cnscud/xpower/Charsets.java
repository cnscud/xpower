package com.cnscud.xpower;

import java.io.UnsupportedEncodingException;

public enum Charsets {
	UTF8("UTF-8"), //
	GBK("GBK"), //
	ISO8859("ISO8859-1");

	public final String charset;

	private Charsets(String value) {
		this.charset = value;
	}
	
	public String toString(byte[] b) {
		try {
			return new String(b,charset);
		} catch (UnsupportedEncodingException e) {
			throw new IllegalStateException(e);
		}
	}
	
	public byte[] getBytes(String s) {
		try {
			return s.getBytes(charset);
		} catch (UnsupportedEncodingException e) {
			throw new IllegalStateException(e);
		}
	}
}