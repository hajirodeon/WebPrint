package mslinks.extra;

import io.ByteReader;
import io.ByteWriter;

import java.io.IOException;

import mslinks.Serializable;
import mslinks.ShellLinkException;
import mslinks.data.GUID;

public class Tracker implements Serializable {
	
	public static final int signature = 0xA0000003;
	public static final int size = 0x60;
	
	private String netbios;
	private GUID d1, d2, db1, db2;
	
	public Tracker() {
		netbios = "localhost";
		d1 = db1 = new GUID();
		d2 = db2 = new GUID("539D9DC6-8293-11E3-8FB0-005056C00008");
	}
	
	public Tracker(ByteReader br, int sz) throws ShellLinkException, IOException {
		if (sz != size)
			throw new ShellLinkException();
		int len = (int)br.read4bytes();
		if (len < 0x58)
			throw new ShellLinkException();
		br.read4bytes();
		int pos = br.getPosition();
		netbios = br.readString(16);
		br.seek(pos + 16 - br.getPosition());
		d1 = new GUID(br);
		d2 = new GUID(br);
		db1 = new GUID(br);
		db2 = new GUID(br);
	}

	@Override
	public void serialize(ByteWriter bw) throws IOException {
		bw.write4bytes(size);
		bw.write4bytes(signature);
		bw.write4bytes(0x58);
		bw.write4bytes(0);
		byte[] b = netbios.getBytes();
		bw.writeBytes(b);
		for (int i=0; i<16-b.length; i++)
			bw.write(0);
		d1.serialize(bw);
		d2.serialize(bw);
		db1.serialize(bw);
		db2.serialize(bw);
	}
	
	public String getNetbiosName() { return netbios; }
	public Tracker setNetbiosName(String s) throws ShellLinkException {
		if (s.length() > 16)
			throw new ShellLinkException("netbios name length must be <= 16");
		netbios = s;
		return this;
	}
}
