/*******************************************************************************
 * 보안동의 페이지 메인 로직
 ******************************************************************************/

// 언어팩 선언.
$.lang = {};

$.lang.ko = {
    info: 'H·able(이하 ‘본 프로그램’) 사용에 앞서 다음 내용을 충분히 숙지하신 후, 동의 여부에 체크 해주시기 바랍니다.<br><br>■ 보안 정책 및 유의 사항<br><br>1. 본 프로그램은 회사의 업무를 지원하기 위해 제공되는 프로그램으로, 이용자는 이를 회사 업무 처리 목적으로만 사용하여야 하며, 개인의 사적인 용도로는 사용할 수 없습니다.<br>2. 임직원은 당사 모바일 기기 준수 사항에 따라 모바일 기기 보호를 위한 화면 잠금 설정, 임의 개조 금지, 백신 설치 및 시스템 패치 등을 따라야 합니다.<br>3. 이용자는 회사의 보안 규정(모바일 보안 지침 등)에 따라 본 프로그램 상의 제반 정보(가동현황, 고장현황, 재고정보 등)가 유출되지 않도록 보안을 철저히 유지해야 합니다.<br><br>  본인은 상기와 같은 회사의 보안정책 관한 사항을 충분히 읽고 숙지하였으며, 상기와 같은 회사의 보안정책에 대하여 동의합니다.',
	yes: '예',
	no: '아니오',
};

$.lang.en = {
	info: 'Please, read this agreement carefully. You must agree to the terms of this agreement to access \'H·able\'.<br><br>■ Security Policy<br><br>1. This program is provided to support the company\'s business. Users must use it only for the business purpose and user cannot be used for personal purpose.<br>2. Executives and employees must follow the company\'s mobile device compliance policies to protect their mobile devices. Mobile device compliance includes:Requiring Screen Lock setting Prohibitting arbitrary modifications Installing vaccines Patching system<br>3. The user must keep your security thoroughly in order to prevent any leakage of information (operation status, breakdown status, inventory information, etc.) on this program in accordance with the company\'s security policy.<br><br>I have read and fully understood the above company\'s security policy, and I agree with the above company\'s security policy.',
	yes: 'Yes',
	no: 'No',
};
	
$.lang.zh = {
	info: '使用H·able(以下称‘本程序’) 之前请充分阅读以下内容并在同意与否打钩.<br> <br>■ 保安政策和留意事项<br> <br>1.本程序是为了支持公司业务而提供的，使用人只能为了处理公司业务而使用，不可因个人用途使用.<br>2.使用任意须根据公司保安规定(Mobile保安指南等)防止流失本程序的各种信息（稼动现况、故障现况、库存信息等）必须维护保安.<br>	<br>本人已阅读上述公司保安政策相关的事项并同意上述公司保安政策.',
	yes: '是',
	no: '不是',
};

$.lang.es = {
	info: 'Please, read this agreement carefully. You must agree to the terms of this agreement to access \'H·able\'.<br><br>■ Security Policy<br><br>1. This program is provided to support the company\'s business. Users must use it only for the business purpose and user cannot be used for personal purpose.<br>2. Executives and employees must follow the company\'s mobile device compliance policies to protect their mobile devices. Mobile device compliance includes:Requiring Screen Lock setting Prohibitting arbitrary modifications Installing vaccines Patching system<br>3. The user must keep your security thoroughly in order to prevent any leakage of information (operation status, breakdown status, inventory information, etc.) on this program in accordance with the company\'s security policy.<br><br>I have read and fully understood the above company\'s security policy, and I agree with the above company\'s security policy.',
	yes: 'Yes',
	no: 'No',
};

function setLanguage(currentLanguage) {
  console.log('setLanguage', arguments);
  $('[data-langNum]').each(function() {
    var $this = $(this); 
    $this.html($.lang[currentLanguage][$this.data('langnum')]); 
  });	
}
var heum002 = (function(window, document, $, M, undefined) {
	// 화면 초기화
	var setInitScreen = function() {
		var userLang = navigator.language || navigator.userLanguage;
		userLang = userLang.toLowerCase().substring(0,2); 
		if("ko,en,zh".indexOf(userLang)==-1)
			userLang = "en";
		$(".secure_info").html($.lang[userLang].info);
		$("#btn_yes").html($.lang[userLang].yes);
		$("#btn_no").html($.lang[userLang].no);
		
	}; // setInitScreen

	// 이벤트 초기화
	var setInitEvent = function() {
		$("#btn_yes").click(function() {
			window.location.replace("resources/app/H_able_Client.apk");
		});
		$("#btn_no").click(function() {
			window.location.replace("login.jsp");
		});
	};

	// Public Method
	return {
		setInitScreen : setInitScreen,
		setInitEvent : setInitEvent
	};

})(window, document, $, M);

/*******************************************************************************
 * MCore Common Events
 ******************************************************************************/
M.onReady(function() {
	heum002.setInitScreen();
	heum002.setInitEvent();
}).onHide(function() {
}).onResume(function() {

}).onPause(function() {

}).onRestore(function() {
}).onDestroy(function() {
}).onBack(function() {
}).onKey(function() {

});
