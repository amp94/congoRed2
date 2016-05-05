/**
 * This file is where you define your application routes and controllers.
 * 
 * Start by including the middleware you want to run for every request;
 * you can attach middleware to the pre('routes') and pre('render') events.
 * 
 * For simplicity, the default setup for route controllers is for each to be
 * in its own file, and we import all the files in the /routes/views directory.
 * 
 * Each of these files is a route controller, and is responsible for all the
 * processing that needs to happen for the route (e.g. loading data, handling
 * form submissions, rendering the view template, etc).
 * 
 * Bind each route pattern your application should respond to in the function
 * that is exported from this module, following the examples below.
 * 
 * See the Express application routing documentation for more information:
 * http://expressjs.com/api.html#app.VERB
 */

var keystone = require('keystone');
var middleware = require('./middleware');
var importRoutes = keystone.importer(__dirname);
var exec = require('child_process').execFile;
var dialog = require('dialog');


// Common Middleware
keystone.pre('routes', middleware.initLocals);
keystone.pre('render', middleware.flashMessages);

// Import Route Controllers
var routes = {
	views: importRoutes('./views')
};

// Setup Route Bindings
exports = module.exports = function(app) {
	
	// Views
	app.get('/', routes.views.index);
	app.get('/test', middleware.requireUser, routes.views.test);
	
	//app.post('/api/foo', foo);
	app.get('/api/foo', foo);
	
	var spawn  = require('child_process').spawn;

	function foo(req, res){
		// console.log("fun2() start");
		var args = req.query.data;
		
		args = args.replace(/\\/g,"");
		args = args.replace(/\'/g,"");
		args = args.replace(/\"/g,"");
		// console.log("args"+args);
		var process = args.split(",");
		var results = "Image       | Results\n---------------------"
		var resultsDone = 0;
		// console.log(results);
		for (var i = 0; i <= process.length-1; i++) {
			// console.log(i)
			exec('java' ,['-jar','./algorithms/Algorithm1.jar',process[i]], function(err, stdout, stderr) {
				resultsDone += 1;
				if (resultsDone % 5 ==0){
					setTimeout(function() {
					    console.log('Waiting');
					}, 3000);
				}
				if (err!=null){  
					console.log("ERROR",err)
				}
				else{
					// console.log(stdout);
					
					var name = stdout.substring(0,stdout.indexOf(' '))
					var result = stdout.substring(stdout.indexOf('|')+2,stdout.indexOf('|')+6)
					var url = stdout.substring(stdout.lastIndexOf('|')+7)
					console.log(name,result,url)
					Results = keystone.list('Results');
					 
					var newResult = new Results.model({
						  img_name:  name,
						  img_url: url,
						  result:   result
					});
					 
					newResult.save(function(err) {
					    console.log("Result has been saved") // post has been saved	
					});
				}          
			});
		};
		res.send("/results");
	};
	// NOTE: To protect a route so that only admins can see it, use the requireUser middleware:
	// app.get('/protected', middleware.requireUser, routes.views.protected);
	
};
