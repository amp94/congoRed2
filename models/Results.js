var keystone = require('keystone');
var Types = keystone.Field.Types;

/**
 * Results Model
 * ==========
 */

var Results = new keystone.List('Results',{ sortable: true, defaultSort: 'img_name' 
});


Results.add({
  img_name:  { type: String, required: true },
  img_url: { type: Types.Url, required: true },
  result:   { type: String, required: true },
  publishedDate: { type: Date, default: Date.now },
});

// Provide access to Keystone


/**
 * Registration
 */
Results.defaultColumns = 'img_name, result, img_url';


Results.register();
