<?xml version='1.0' encoding='UTF-8'?>

<adb>
	<uiwrapper name="def" />

	<property name="m_lResID" id="id"/>

	<property name="filename" wrap="def" sortdesc="1" sortasc="0">
		<label lsid="CN">名称</label>
		<label lsid="EN">Name</label>

		<out>
			<align>left</align>
		</out>
	</property>

	<property name="m_strContentType" sortdesc="3" sortasc="2">
		<label lsid="CN">内容类型</label>
		<label lsid="EN">Content Type</label>

		<out>
			<align>left</align>
		</out>
	</property>

	<property name="size" wrap="def" sortdesc="5" sortasc="4">
		<label lsid="CN">大小</label>
		<label lsid="EN">Size</label>

		<out>
			<align>right</align>
		</out>
	</property>

	<property name="width" wrap="def" sortdesc="7" sortasc="6">
		<label lsid="CN">宽度</label>
		<label lsid="EN">Width</label>

		<out>
			<align>right</align>
		</out>
	</property>

	<property name="height" wrap="def" sortdesc="9" sortasc="8">
		<label lsid="CN">高度</label>
		<label lsid="EN">Height</label>

		<out>
			<align>right</align>
		</out>
	</property>

	<property name="smallIcon" wrap="def">
		<label lsid="CN">预览</label>
		<label lsid="EN">Preview</label>

		<out>
			<align>right</align>
			<width>100%</width>
		</out>
	</property>

	<group id="list">
		<propref name="filename" />
		<propref name="m_strContentType" />
		<propref name="size" />
		<propref name="width" />
		<propref name="height" />
		<propref name="smallIcon" />
	</group>
</adb>
