Dropzone.autoDiscover = false;
$(document).ready(function() {
	$("#upload-payment-receipt-image").dropzone({
		url : "uploadFile",
		addRemoveLinks : true,
		acceptedFiles: ".pdf",
		success : function(file, response) {
			if (response.status === 'SUCCESS') {
				window.location.href = "files";
			} else {

			}
		},
		error : function(file, response) {
			file.previewElement.classList.add("dz-error");
		}
	});
});