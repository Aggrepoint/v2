<?xml version='1.0' encoding='UTF-8'?>

<boundle>
	<msg name="initFailed">
		<text lsid="CN">初始化失败。</text>
		<text lsid="EN">Initialization failed.</text>
	</msg>

	<msg name="uriNotDefined(requestPath)">
		<text lsid="CN">请求未定义的路径：(requestPath)。</text>
		<text lsid="EN">Request not defined for request: (requestPath).</text>
	</msg>

	<msg name="winStateNotFound(requestPath, winState)">
		<text lsid="CN">根据路径(requestPath)找不到对应的窗口状态：(winState)。</text>
		<text lsid="EN">Win state (winState) not found for request: (requestPath).</text>
	</msg>

	<msg name="loadModuleFailed(requestPath)">
		<text lsid="CN">加载模块失败，请求路径：(requestPath)。</text>
		<text lsid="EN">Load module failed for request: (requestPath).</text>
	</msg>

	<msg name="retCodeNotDefined(requestPath, retCode)">
		<text lsid="CN">找不到响应码(retCode)的定义，请求路径：(requestPath)。</text>
		<text lsid="EN">Return code (retCode) not defined for requst: (requestPath).</text>
	</msg>

	<msg name="throwRetCodeNotDefined(requestPath, throw)">
		<text lsid="CN">找不到对应抛出响应码(throw)的定义，请求路径：(requestPath)。</text>
		<text lsid="EN">Throw return code for (throw) not defined for requst: (requestPath).</text>
	</msg>

	<msg name="presentError(requestPath, retCode)">
		<text lsid="CN">处理展现失败，请求路径：(requestPath)，响应码：(retCode)。</text>
		<text lsid="EN">Present error, request: (requestPath), return code: (retCode).</text>
	</msg>

	<msg name="stateNotDefined(requestPath, state)">
		<text lsid="CN">找不到状态(state)的定义，请求路径：(requestPath)。</text>
		<text lsid="EN">State (state) not defined for request: (requestPath).</text>
	</msg>

	<msg name="userProfileError(requestPath)">
		<text lsid="CN">获取用户身份失败，请求路径：(requestPath)。</text>
		<text lsid="EN">Error get user profile for request: (requestPath).</text>
	</msg>

	<msg name="psnEngineError(requestPath)">
		<text lsid="CN">构造个性化引擎失败，请求路径：(requestPath)。</text>
		<text lsid="EN">Error constructing psn engine for request: (requestPath).</text>
	</msg>

	<msg name="systemRetCodeNotDefined(retCode)">
		<text lsid="CN">找不到系统响应码(retCode)的定义。</text>
		<text lsid="EN">System return code (retCode) not defined.</text>
	</msg>

	<msg name="systemUserProfileError(retCode)">
		<text lsid="CN">为系统响应码(retCode)构造获取用户身份失败。</text>
		<text lsid="EN">Error get user profile for system return code (retCode).</text>
	</msg>

	<msg name="systemPsnEngineError(retCode)">
		<text lsid="CN">为系统响应码(retCode)构造个性化引擎失败。</text>
		<text lsid="EN">Error constructing psn engine for system return code (retCode).</text>
	</msg>

	<msg name="systemPresentError(retCode, exception)">
		<text lsid="CN">展示错误响应码(retCode)时遇到异常(exception)。</text>
		<text lsid="EN">Encounter exception (exception) while processing system error (retCode).</text>
	</msg>

	<msg name="winIIDNotDefined(requestPath)">
		<text lsid="CN">未指定窗口实例编号，请求路径：(requestPath)</text>
		<text lsid="EN">Window instance id not specified in request: (requestPath).</text>
	</msg>

	<msg name="winInstanceNotExist(requestPath, winiid)">
		<text lsid="CN">窗口实例不存在。请求路径：(requestPath)，窗口实例编号：(winiid)</text>
		<text lsid="EN">Window instance not exists. Request: (requestPath), Window instance id: (winiid).</text>
	</msg>

	<msg name="getLoggerError(requestPath)">
		<text lsid="CN">获取Logger失败，请求路径：(requestPath)</text>
		<text lsid="EN">Get logger failed for request: (requestPath).</text>
	</msg>

	<msg name="prepareReqForCreateWinletError(requestPath)">
		<text lsid="CN">为创建Winlet准备请求对象失败，请求路径：(requestPath)</text>
		<text lsid="EN">Prepare request for create winlet failed on request: (requestPath).</text>
	</msg>

	<msg name="createWinletError(requestPath)">
		<text lsid="CN">创建Winlet失败，请求路径：(requestPath)</text>
		<text lsid="EN">Create winlet failed on request: (requestPath).</text>
	</msg>

	<msg name="loadWinletExtDefError(winletClass)">
		<text lsid="CN">加载Winlet (winletClass) 的外部定义遇到异常。</text>
		<text lsid="EN">Error loading external definition for winlet (winletClass).</text>
	</msg>
</boundle>
