/*******************************************************************************
 * 로그인 페이지 메인 로직
 ******************************************************************************/
var korea_allow = false; //한국법인 노출여부
var user_id;
var comp;

var heum001 = (function(window, document, $, M, undefined) {
	// 화면 초기화
	var setInitScreen = function() {
		if (!detectMobile()) {
			alert("You can access  only available on mobile!");
			window.location.replace("http://www.hyundai-transys.com");
		}
		if(korea_allow){
			$("#comp").prepend('<option value="HPT" class="listComp1">Korea</option>');
			$("#comp option[value='HPT']").prop("selected","selected");
			setCompImage();
		}
	}; // setInitScreen

	// 이벤트 초기화
	var setInitEvent = function() {
		var signBtn = $("#btn_signIn");
		signBtn.click(function() {
			loginProcess();
		});

		$("#comp").change(function() {
			setCompImage();
		});

		$("#id").keyup(function(e) {
			var inputValue = $(this).val();
		});

		$("#pw").keyup(function(e) {
			var inputValue = $(this).val();
			var charKeyCode = getKeyCode(e, inputValue);
			if (charKeyCode === 13) {// enter clicked
				$(signBtn).click();
			}
		});

		var getKeyCode = function(e, str) {
			var charKeyCode = e.keyCode || e.which;
			return charKeyCode;
		}
	};

	// 회사선택 이미지 설정
	var setCompImage = function() {
		var compi_src = "./img/" + $("#comp").val() + ".png";
		$("#compIcon").prop("src", compi_src);
	}

	// 로그인 (process)
	var loginProcess = function() {
		var id, pw;
		id = $("#id").val().trim();
		pw = $("#pw").val().trim();
		comp = $("#comp").val();
		if (id == "") {
			alert("You should type in ID.");
			$("#id").val("");
			$("#id").focus();
		} else if (pw == "") {
			alert("You should type in PW.");
			$("#pw").val("");
			$("#pw").focus();
		} else {
			storage = Storages.localStorage;
			document.frmMain.COMMAND.value = "LOGIN";
			document.frmMain.IS_AGREE.value = storage.get('is_agree');
			document.frmMain.action = "./loginLanding.do";
			document.frmMain.submit();
			$('body').loading({
		          stoppable: false
		    });
		}
	};

	var detectMobile = function() {
		if (navigator.userAgent.match(/Android/i)
				|| navigator.userAgent.match(/iPhone/i)
				|| navigator.userAgent.match(/iPad/i)
				|| navigator.userAgent.match(/iPod/i)) {
			return true;
		} else {
			return false;
		}
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
	heum001.setInitScreen();
	heum001.setInitEvent();
}).onHide(function() {
}).onResume(function() {

}).onPause(function() {

}).onRestore(function() {
}).onDestroy(function() {
}).onBack(function() {
}).onKey(function() {

});
