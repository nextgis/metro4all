$(document).ready(function () {

	$('.btn-confirm').click(function () {
		if(! $('#modal-confirm').length) {
			$('body').append('<div id="modal-confirm" class="modal fade"><div class="modal-dialog"><div class="modal-content"><div class="modal-body"><p>Действительно удалить?</p></div><div class="modal-footer"><button type="button" class="btn btn-default" data-dismiss="modal">Нет</button><a type="button" class="btn btn-primary">Да</a></div></div></div></div>');
		}
		$('#modal-confirm .btn.btn-primary').attr('href', $(this).attr('confirm-href'));
		$('#modal-confirm .modal-body p').html($(this).attr('confirm-question'));
		$('#modal-confirm').modal();
	});

});