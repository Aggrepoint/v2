<?xml version='1.0' encoding='UTF-8'?>

<adb>
	<uiwrapper name="def" />

	<property name="m_lWindowID" id="wid" />

	<property name="m_strName" id="name">
		<label lsid="CN">名称</label>
		<label lsid="EN">Name</label>

		<out>
			<align>left</align>
		</out>

		<in>
			<text width="20" maxlength="100" />

			<validator id="ne" skip="next">
				<msg lsid="CN">请输入窗口名。</msg>
				<msg lsid="CN">Please input window name.</msg>
			</validator>
		</in>
	</property>

	<property name="m_strDesc" id="desc">
		<label lsid="CN">说明</label>
		<label lsid="EN">Description</label>

		<out>
			<align>left</align>
		</out>

		<in>
			<textarea rows="4" cols="80" markup="html" />

			<validator id="maxlen">
				<arg>300</arg>

				<msg lsid="CN">说明不能超过300个字符。</msg>
				<msg lsid="EN">
					Description can't contain more than 300 characters.
				</msg>
			</validator>
		</in>
	</property>

	<property name="m_strURL" id="url">
		<label lsid="CN">URL</label>
		<label lsid="EN">URL</label>

		<out>
			<align>left</align>
		</out>

		<in>
			<text width="20" maxlength="50" />

			<validator id="ne" skip="next">
				<msg lsid="CN">请输入URL。</msg>
				<msg lsid="CN">Please input URL.</msg>
			</validator>
		</in>
	</property>

	<property name="m_strAccessRule" id="rule">
		<label lsid="CN">访问规则</label>
		<label lsid="EN">Access Rule</label>

		<out>
			<align>left</align>
		</out>

		<in>
			<textarea rows="4" cols="80" markup="html" />

			<validator id="ne" skip="next">
				<msg lsid="CN">请输入。</msg>
				<msg lsid="CN">Please input.</msg>
			</validator>

			<validator id="maxlen">
				<arg>500</arg>

				<msg lsid="CN">访问规则不能超过500个字符。</msg>
				<msg lsid="EN">
					Access rule can't contain more than 500 characters.
				</msg>
			</validator>
		</in>
	</property>

	<property name="userProfileFlag" id="flag_up">
		<label lsid="CN">允许通过操作改变用户身份</label>
		<label lsid="EN">Can Change User Via Action</label>

		<in>
			<checkbox />
		</in>
	</property>

	<property name="viewChangeUserFlag" id="flag_vcu">
		<label lsid="CN">允许在显示时改变用户身份</label>
		<label lsid="EN">Can Change User Via View</label>

		<in>
			<checkbox />
		</in>
	</property>

	<property name="dynaTitleFlag" id="flag_dt">
		<label lsid="CN">允许设置窗口标题</label>
		<label lsid="EN">Can Set Window Title</label>

		<in>
			<checkbox />
		</in>
	</property>

	<property name="dynaWinStateFlag" id="flag_ws">
		<label lsid="CN">允许变更窗口模式</label>
		<label lsid="EN">Can Change Window Mode</label>

		<in>
			<checkbox />
		</in>
	</property>

	<property name="minRequestFlag" id="flag_mr">
		<label lsid="CN">最小化时也请求</label>
		<label lsid="EN">Request When Minimized</label>

		<in>
			<checkbox />
		</in>
	</property>

	<property name="supportMaxFlag" id="flag_max">
		<label lsid="CN">支持最大化</label>
		<label lsid="EN">Support Maximized</label>

		<in>
			<checkbox />
		</in>
	</property>

	<property name="supportMinFlag" id="flag_min">
		<label lsid="CN">支持最小化</label>
		<label lsid="EN">Support Minimized</label>

		<in>
			<checkbox />
		</in>
	</property>

	<property name="m_previewImage" wrap="def" wrapname="previewImg" id="img" ajaxvalidate="no">
		<label lsid="CN">预览</label>
		<label lsid="EN">Preview</label>

		<remarks lsid="CN">可选。图片文件必须是JPG或GIF格式，大小不能超过500K</remarks>
		<remarks lsid="EN">This is optional. The image file must be a JPG or
			GIF file less than 500K</remarks>

		<out>
			<wrap>no</wrap>
			<width>5%</width>
		</out>

		<in>
			<file width="30" maxsize="" />
			<validator method="checkPreviewImg" />
		</in>
	</property>

	<group id="list">
		<propref name="m_strName" />
		<propref name="m_strURL" />
		<propref name="m_strAccessRule" />
		<propref name="m_previewImage" />
	</group>

	<group id="edit">
		<propref name="m_strName" />
		<propref name="m_strDesc" />
		<propref name="m_strURL" />
		<propref name="m_strAccessRule" />
		<propref name="userProfileFlag" />
		<propref name="viewChangeUserFlag" />
		<propref name="dynaTitleFlag" />
		<propref name="dynaWinStateFlag" />
		<propref name="minRequestFlag" />
		<propref name="supportMaxFlag" />
		<propref name="supportMinFlag" />
		<propref name="m_previewImage" />
	</group>
</adb>
