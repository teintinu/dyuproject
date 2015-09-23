# `Regular Google Accounts (gmail)` #

https://www.google.com/accounts/o8/id is commonly used.

Its the url of the xrds file.


# `Google Accounts OpenId Federated Login` #

You actually have 3 options for the openid identifier:
  * https://www.google.com/accounts/o8/site-xrds?hd=example.com

> This is the url of the xrds file.  Works out-of-the-box.

  * https://www.google.com/a/example.com/o8/ud?be=o8

> This is the actual openid2.provider url.

> To enable using the openid\_identifier as the openid2.provider, edit openid.properties:

> `openid.identifier_as_server = true`

  * http://www.example.com

> With this approach, you are allowing your users to use your site as the openid identifier.

> To enable, simply add this to your main page:

> `<link rel="openid2.provider" href="https://www.google.com/a/example.com/o8/ud?be=o8" />`