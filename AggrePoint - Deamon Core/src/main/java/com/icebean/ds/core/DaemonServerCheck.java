package com.icebean.ds.core;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Enumeration;
import java.util.Vector;

import org.apache.log4j.Category;

import aiismg.jcmppapi.CMPPAPI;

import com.icebean.core.common.Log4jIniter;

/**
 * 作为独立应用程序运行，检查Daemon服务器及其中的Daemon是否正常工作。若Daemon不正常工作，则 发送报警短信到指定的手机号码
 * 
 * @author YJM
 */
public class DaemonServerCheck {
	/** 日志 */
	static Category m_log = Log4jIniter.getCategory();

	/**
	 * 运行参数： -server 随后每个以空格分隔的参数表示要检查的服务器的名称和完整检查URL，名称与URL间以冒号分隔 -phone
	 * 随后每个以空格分隔的参数表示要发送报警短信的手机号码 -time 随后一个参数表示以秒为单位的检查时间间隔 -smcfg
	 * 随后一个参数表示短信服务配置文件 -smnum 随后一个参数表示使用的短信服务特服号
	 */
	public static void main(String[] args) {
		// 要检查的服务器名称
		Vector<String> vecServers = new Vector<String>();
		// 要检查的服务器的URL，与vecServers一一对应
		Vector<String> vecUrls = new Vector<String>();
		// 要通知的手机号码
		Vector<String> vecPhones = new Vector<String>();
		// 检查时间间隔
		long lTime = 30 * 60 * 1000l;
		// 短信服务配置文件
		String strSmCfgFile = null;
		// 短信服务特服号
		String strSmNum = null;

		String str;

		// {从命令行参数获取要检查的服务器地址和要通知的手机号码以及检查时间间隔
		try {
			if (args.length < 6)
				throw new Exception();

			int iMode = 0;
			for (int i = 0; i < args.length; i++) {
				if (args[i].equals("-server")) {
					iMode = 1;
					continue;
				}
				if (args[i].equals("-phone")) {
					iMode = 2;
					continue;
				}
				if (args[i].equals("-time")) {
					iMode = 3;
					continue;
				}
				if (args[i].equals("-smcfg")) {
					iMode = 4;
					continue;
				}
				if (args[i].equals("-smnum")) {
					iMode = 5;
					continue;
				}

				switch (iMode) {
				case 0:
					throw new Exception();
				case 1:
					int idx = args[i].indexOf(":");
					if (idx < 0)
						throw new Exception();
					vecServers.add(args[i].substring(0, idx));
					vecUrls.add(args[i].substring(idx + 1));
					break;
				case 2:
					vecPhones.add(args[i]);
					break;
				case 3:
					lTime = Integer.parseInt(args[i]) * 1000l;
					iMode = 0;
					break;
				case 4:
					strSmCfgFile = args[i];
					iMode = 0;
					break;
				case 5:
					strSmNum = args[i];
					iMode = 0;
					break;
				}
			}

			if (vecServers.size() == 0 || vecUrls.size() != vecServers.size()
					|| vecPhones.size() == 0 || strSmCfgFile == null
					|| strSmNum == null)
				throw new Exception();
		} catch (Exception e) {
			System.out.println("运行参数：");
			System.out
					.println("-server 随后每个以空格分隔的参数表示要检查的服务器的名称和完整检查URL，名称与URL间以冒号分隔");
			System.out.println("-phone 随后每个以空格分隔的参数表示要发送报警短信的手机号码");
			System.out.println("-time 随后一个参数表示以秒为单位的时间间隔");
			System.out.println("-smcfg 随后一个参数表示短信服务配置文件");
			System.out.println("-smnum 随后一个参数表示使用的短信服务特服号");
			return;
		}
		// }

		m_log.debug("开始运行，运行参数为：");
		for (Enumeration<String> enum1 = vecServers.elements(), enum2 = vecUrls
				.elements(); enum1.hasMoreElements() && enum2.hasMoreElements();)
			m_log.debug("检查服务器" + enum1.nextElement() + "："
					+ enum2.nextElement());
		for (Enumeration<String> enm = vecPhones.elements(); enm
				.hasMoreElements();)
			m_log.debug("通知手机号码：" + enm.nextElement());
		m_log.debug("检查时间间隔：" + lTime + "毫秒");
		m_log.debug("短信服务配置文件：" + strSmCfgFile);
		m_log.debug("短信特服号：" + strSmNum);

		while (true) {
			try {
				// 等待检查时间间隔
				m_log.debug("检查前等待" + lTime + "毫秒");
				try {
					Thread.sleep(lTime);
				} catch (InterruptedException e) {
				}

				// 要发送的报警信息
				Vector<String> vecAlertMessages = new Vector<String>();

				// {检查
				for (Enumeration<String> enm = vecServers.elements(), enumUrl = vecUrls
						.elements(); enm.hasMoreElements()
						&& enumUrl.hasMoreElements();) {
					String strServerName = enm.nextElement();
					String strUrl = enumUrl.nextElement();

					m_log.debug("开始检查服务器：" + strServerName);

					try {
						URL url = new URL(strUrl);

						HttpURLConnection conn = (HttpURLConnection) url
								.openConnection();
						conn.setRequestProperty("connection", "close");
						conn.connect();

						String strStatus = new String(conn.getHeaderField(
								DaemonServer.HEADER_STATUS).getBytes(
								"ISO8859_1"), "GBK");

						m_log.debug("服务器状态：" + strStatus);

						conn.disconnect();

						if (strStatus.startsWith("0")) {
							m_log.debug("服务器运行正常：" + strServerName);
							continue;
						}

						// 存在不正常的Daemon
						str = "服务器\"" + strServerName + "\"上以下Daemon不正常："
								+ strStatus.substring(2);

						m_log.error(str);
						vecAlertMessages.add(str);
					} catch (Exception e) {
						str = "在检查服务器\"" + strServerName + "\"过程中遇到异常";

						m_log.error(str, e);
						vecAlertMessages.add(str + ":" + e.toString());
					}
				}
				// }

				// {发送报警信息
				if (vecAlertMessages.size() != 0) {
					CMPPAPI api = new CMPPAPI();
					if (api.InitCMPPAPI(strSmCfgFile) != 0)
						throw new Exception("初始化CMPPAPI失败。");

					for (Enumeration<String> enm = vecAlertMessages.elements(); enm
							.hasMoreElements();) {
						String strMessage = enm.nextElement();
						for (Enumeration<String> enum2 = vecPhones.elements(); enum2
								.hasMoreElements();) {
							str = enum2.nextElement();
							m_log.debug("将消息\"" + strMessage + "\"通知手机" + str);
							send(api, strSmNum, str, strMessage);
						}
					}
				}
				// }
			} catch (Throwable t) {
				m_log.error("遇到异常", t);
			}
		}
	}

	public static int send(CMPPAPI api, String source, String dest, String msg) {
		// 源号码
		byte[] sSrcTermID = new byte[21];
		System.arraycopy(source.getBytes(), 0, sSrcTermID, 0, source.length());
		// 目的号码
		byte[] sDestTermID = new byte[21];
		System.arraycopy(dest.getBytes(), 0, sDestTermID, 0, dest.length());
		byte[] sMsgID = new byte[200];
		// 最大信息长度
		int maxLength = 70;
		String strNextPage = "(见下页)";

		int iPageLen = maxLength - strNextPage.length();
		String strAppend;
		int retcode;

		int len;
		for (int iStart = 0; iStart < msg.length();) {
			if (iStart + maxLength >= msg.length()) {
				len = msg.length() - iStart;
				strAppend = "";
			} else {
				len = iPageLen;
				strAppend = strNextPage;
			}

			// 内容
			byte[] sMsgContent = (msg.substring(iStart, iStart + len) + strAppend)
					.getBytes();
			int nMsgLen = sMsgContent.length;

			retcode = api.CMPPSendSingle((byte) 0, (byte) 2, "0002".getBytes(),
					(byte) 15, "01".getBytes(), "0000".getBytes(), ""
							.getBytes(), "".getBytes(), sSrcTermID,
					sDestTermID, nMsgLen, sMsgContent, sMsgID, (byte) 2, ""
							.getBytes(), (byte) 0, (byte) 0);
			if (retcode != 0)
				return retcode;
			iStart += len;
		}

		return 0;
	}
}
