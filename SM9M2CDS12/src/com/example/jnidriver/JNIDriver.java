package com.example.jnidriver;

public class JNIDriver {

	private boolean mConnectFlag;
	private boolean mConnectFlagFND;
	private boolean mConnectFlagPZ;
	
	static {
		System.loadLibrary("JNIDriver");
	}
	
	private native static int openDriver(String path);
	private native static int openDriverFND(String path);
	private native static int openDriverPZ(String path);
	private native static void closeDriver();
	private native static void closeDriverFND();
	private native static void closeDriverPZ();
	private native static void writeDriver(byte[] data, int length);
	private native static void writeDriverFND(byte[] data, int length);
	private native void setBuzzer(char data);
	
	public JNIDriver() {
		mConnectFlag = false;
	}
	
	public int open(String driver) {
		if (mConnectFlag) return -1;
		
		if (openDriver(driver) > 0) {
			mConnectFlag = true;
			return 1;
		} else {
			return -1;
		}
	}
	
	public int openFND(String driver) {
		if (mConnectFlagFND) return -1;
		
		if (openDriverFND(driver) > 0) {
			mConnectFlagFND = true;
			return 1;
		} else {
			return -1;
		}
	}
	
	public int openPZ(String driver) {
		if (mConnectFlagPZ) return -1;
		
		if (openDriverPZ(driver) > 0) {
			mConnectFlagPZ = true;
			return 1;
		} else {
			return -1;
		}
	}
	
	public void close() {
		if (!mConnectFlag) return;
		mConnectFlag = false;
		closeDriver();
	}
	
	public void closeFND() {
		if (!mConnectFlagFND) return;
		mConnectFlagFND = false;
		closeDriverFND();
	}
	
	public void closePZ() {
		if (!mConnectFlagPZ) return;
		mConnectFlagPZ = false;
		closeDriverPZ();
	}
	
	protected void finalize() throws Throwable {
		close();
		closeFND();
		closePZ();
		super.finalize();
	}
	
	public void write(byte[] data) {
		if (!mConnectFlag) return;
		writeDriver(data, data.length);
	}
	
	public void writeFND(byte[] data) {
		if (!mConnectFlagFND) return;
		writeDriverFND(data, data.length);
	}
	
	public void setBuzzer(int val) {
		if(!mConnectFlagPZ) return;
		if(val<1)
			val = 1;
		if(val>22)
			val = 22;
		setBuzzer((char)val);
	}
}
