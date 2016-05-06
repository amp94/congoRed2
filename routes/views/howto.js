var keystone = require('keystone');
var exec = require('child_process').execFile;

exports = module.exports = function(req, res) {
	
	var view = new keystone.View(req, res);
	var locals = res.locals;
	// Set locals
	locals.section = 'howto';
	
	
	// Render the view
	view.render('howto');
	
};
