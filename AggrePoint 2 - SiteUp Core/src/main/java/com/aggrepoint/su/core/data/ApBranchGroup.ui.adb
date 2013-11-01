<?xml version='1.0' encoding='UTF-8'?>

<adb>
	<uiwrapper name="def" />

	<property name="m_lGroupID" id="gid" />

	<property name="m_lBranchID" wrap="def" wrapname="branch" id="bid">
		<label lsid="CN">分支</label>
		<label lsid="EN">Branch</label>

		<in>
			<select />
		</in>
	</property>

	<property name="m_strBranchName" id="bname">
		<label lsid="CN">分支</label>
		<label lsid="EN">Branch</label>
	</property>

	<property name="m_iMarkupType" wrap="def" wrapname="markup" id="markup">
		<label lsid="CN">标记类型</label>
		<label lsid="EN">Markup Type</label>
	</property>

	<property name="m_iOrder" id="order">
		<label lsid="CN">顺序</label>
		<label lsid="EN">Order</label>

		<in>
			<number width="5" maxlength="5" />

			<validator id="ne" skip="next">
				<msg lsid="CN">请输入顺序。</msg>
				<msg lsid="CN">Please input order.</msg>
			</validator>
		</in>
	</property>

	<property name="m_strRule" id="rule">
		<label lsid="CN">适配规则</label>
		<label lsid="EN">Rule</label>

		<in>
			<text width="100" maxlength="255" />
		</in>
	</property>

	<group id="list">
		<propref name="m_strBranchName" />
		<propref name="m_iMarkupType" />
		<propref name="m_iOrder" />
		<propref name="m_strRule" />
	</group>

	<group id="edit">
		<propref name="m_lBranchID" />
		<propref name="m_iOrder" />
		<propref name="m_strRule" />
	</group>
</adb>
