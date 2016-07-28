#!/bin/sh

# TODO...WITH FIRST COMMAND...READ THE RESPONSE AND EXTRACT THE COOKIE!!!

curl -v -H "Accept: application/vnd.hmrc.1.0+json" -H "Authorization: Bearer XlLM91CY3hEHqHlrKX9N0TiAuGlHY+r1Z0V6dPGYp+HUTwfljqSIGXa5/G66aQDznkvjiyyqma5/uhk+9Ea+VX5e4SPeYnpLMUzba3A9rRdAP1ZG3anD4zsjuiOmn+uageiBNoR9SjbiHIPmIDVEO+J2n+lgG78UbJZE+26cee81TnU/CoN5cH3wc88qbn82" http://localhost:8238/api/async/start

curl -v -H "Accept: application/vnd.hmrc.1.0+json" -H "Authorization: Bearer XlLM91CY3hEHqHlrKX9N0TohLKzt57ophx4W62K6lRiMrV71X16XVl8jUDOTCHbsn0V0tNEr+Gw+k01F7tp0w+n9j6TPmyuoV0Y9SWCHE3x3VO23f9E1exyOemKuxkww54wWjE50rt/Cx+LBVaq1j/yoAKA2XTy6o2FFqbIg6Rg1TnU/CoN5cH3wc88qbn82" -H "Cookie: mdtp=XJJtKzjsAc2/4db+FT8/sjwzuyac9fyf5Uyxy0VxQpIjWWHUwBTw+GmKv8+CNCc4N9HiWRCF9Df1S7EUWsNjc3vn5j6motm8uvqDldFWZa70NIqEzH5Z;path=/;session=true;secure=false;domain=localhost" http://localhost:8238/api/async/exampleapi?testId=1234

curl -v -H "Accept: application/vnd.hmrc.1.0+json" -H "Authorization: Bearer XlLM91CY3hEHqHlrKX9N0TohLKzt57ophx4W62K6lRiMrV71X16XVl8jUDOTCHbsn0V0tNEr+Gw+k01F7tp0w+n9j6TPmyuoV0Y9SWCHE3x3VO23f9E1exyOemKuxkww54wWjE50rt/Cx+LBVaq1j/yoAKA2XTy6o2FFqbIg6Rg1TnU/CoN5cH3wc88qbn82" -H "Cookie: mdtp=q3iDjgg3AfnA+viAVYq6bud68bz5BEz5l5YMYGlNqWyXF6zchxwI1LvyapFKQJ64R259uaQqayHZpaLvJ1yUDhoD3iE7l5QrFnJBmEzl+FPPdYDWWr4bFzmiu1Mc6eiYMdaeC7i+EK4Jl7gfNAg0gFwOMyEts9kCOtLYaR7bOifLhgLxmSiGHiXfoo0GiJTekAzqz8OzwPaXOvm1rHeZK/7ft5KcnzLbpBp2z5y44QgpY+icn50FjNFi+wKLMuPr8q/xqkvGv1SHdx8sgGc73EUpcQIBDVn0a/UEbuX0232TmyrTv+2D9ZdTt2ugxjs5JA==;path=/;session=true;secure=false;domain=localhost" http://localhost:8238/api/async/poll


#===================
# Routing requests through the api-gateway-proxy.
# http://localhost:8236/oauth/authorize?client_id=some_id&redirect_uri=urn:ietf:wg:oauth:2.0:oob:auto&scope=read:personal-income+read:customer-profile+read:messages+read:submission-tracker+read:web-session+write:push-registration&response_type=code

curl -v -H "Accept: application/vnd.hmrc.1.0+json" -H "Authorization: Bearer 06631b05-6838-4faf-b2b2-bc7c342bc852" http://localhost:8236/api-gateway-async-example/api/async/start

curl -v -H "Accept: application/vnd.hmrc.1.0+json" -H "Authorization: Bearer dea51d13-e6ff-4cfb-95da-86e431c35ce5" -H "Cookie: mdtpapi=IMPTl1Q1xxnnegYY+zXtXegDRof3QkcQbLv1Xf1hd4/8or2FyzbIOiQvWoobzFFzj5WRM0r7kcyp8LhKez/SyE0qZEkyuaZIx4qWuCMF9g6tB2o8HsD2;path=/;session=true;secure=false;domain=localhost" http://localhost:8236/api-gateway-async-example/api/async/exampleapi?testId=1234

curl -v -H "Accept: application/vnd.hmrc.1.0+json" -H "Authorization: Bearer dea51d13-e6ff-4cfb-95da-86e431c35ce5" -H "Cookie: mdtpapi=5T0WMk/L7M0TEnu2du/XY/q9GM9h5IkJaHt+WeCl/myyEsTyu+a7riBj0ziKZ1yqH6d2MC75xfroCH0JQcxWC/5CsQGuqMwLLQ0YJb7IziYqQj3kGgJehTf2i0ffaWJZRxhLMLOA5wheBmE2cGpK4ajdTadeZaa2enRCWkddYNOPOOOXEgfOQMyZrSbXIGDuyK8uB/hao5+9VI51je9RFAgbkZgYTUoh8IV5Sq/mKTp+TEO5ZTYbOu5UsikkEtd6XeqaFmBa1L9tO+V0RlOsTcY/c2vByq8HfQdBYC/GyjbCFhZtf8W9Anu8+a2DYBAK1A==;path=/;session=true;secure=false;domain=localhost" http://localhost:8236/api-gateway-async-example/api/async/poll
