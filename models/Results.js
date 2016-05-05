var keystone = require('keystone');
var Types = keystone.Field.Types;

/**
 * Results Model
 * ==========
 */

var Results = new keystone.List('Results',{ sortable: true, defaultSort: 'img_name' 
});


Results.add({
  img_name:  { type: String, required: true, default: "None" },
  img_url: { type: Types.Url, required: true, default: "None" },
  result:   { type: String, required: true, default: "None" },
  publishedDate: { type: Date, default: Date.now },
});

// Provide access to Keystone


/**
 * Registration
 */
Results.defaultColumns = 'img_name, result';


Results.register();
