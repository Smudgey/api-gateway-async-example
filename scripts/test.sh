#!/bin/sh

# TODO...WITH FIRST COMMAND...READ THE RESPONSE AND EXTRACT THE COOKIE!!!

curl -v -H "Accept: application/vnd.hmrc.1.0+json" -H "Authorization: Bearer XlLM91CY3hEHqHlrKX9N0TohLKzt57ophx4W62K6lRiMrV71X16XVl8jUDOTCHbsn0V0tNEr+Gw+k01F7tp0w+n9j6TPmyuoV0Y9SWCHE3x3VO23f9E1exyOemKuxkww54wWjE50rt/Cx+LBVaq1j/yoAKA2XTy6o2FFqbIg6Rg1TnU/CoN5cH3wc88qbn82" http://localhost:8238/api/async/start

curl -v -H "Accept: application/vnd.hmrc.1.0+json" -H "Authorization: Bearer XlLM91CY3hEHqHlrKX9N0TohLKzt57ophx4W62K6lRiMrV71X16XVl8jUDOTCHbsn0V0tNEr+Gw+k01F7tp0w+n9j6TPmyuoV0Y9SWCHE3x3VO23f9E1exyOemKuxkww54wWjE50rt/Cx+LBVaq1j/yoAKA2XTy6o2FFqbIg6Rg1TnU/CoN5cH3wc88qbn82" -H "Cookie: mdtp=XJJtKzjsAc2/4db+FT8/sjwzuyac9fyf5Uyxy0VxQpIjWWHUwBTw+GmKv8+CNCc4N9HiWRCF9Df1S7EUWsNjc3vn5j6motm8uvqDldFWZa70NIqEzH5Z;path=/;session=true;secure=false;domain=localhost" http://localhost:8238/api/async/exampleapi?testId=1234

curl -v -H "Accept: application/vnd.hmrc.1.0+json" -H "Authorization: Bearer XlLM91CY3hEHqHlrKX9N0TohLKzt57ophx4W62K6lRiMrV71X16XVl8jUDOTCHbsn0V0tNEr+Gw+k01F7tp0w+n9j6TPmyuoV0Y9SWCHE3x3VO23f9E1exyOemKuxkww54wWjE50rt/Cx+LBVaq1j/yoAKA2XTy6o2FFqbIg6Rg1TnU/CoN5cH3wc88qbn82" -H "Cookie: mdtp=q3iDjgg3AfnA+viAVYq6bud68bz5BEz5l5YMYGlNqWyXF6zchxwI1LvyapFKQJ64R259uaQqayHZpaLvJ1yUDhoD3iE7l5QrFnJBmEzl+FPPdYDWWr4bFzmiu1Mc6eiYMdaeC7i+EK4Jl7gfNAg0gFwOMyEts9kCOtLYaR7bOifLhgLxmSiGHiXfoo0GiJTekAzqz8OzwPaXOvm1rHeZK/7ft5KcnzLbpBp2z5y44QgpY+icn50FjNFi+wKLMuPr8q/xqkvGv1SHdx8sgGc73EUpcQIBDVn0a/UEbuX0232TmyrTv+2D9ZdTt2ugxjs5JA==;path=/;session=true;secure=false;domain=localhost" http://localhost:8238/api/async/poll




#===================


curl -v -H "Accept: application/vnd.hmrc.1.0+json" -H "Authorization: Bearer cb44fef9-bea5-47bb-9201-f2c789ab1fa6" http://localhost:8236/api-gateway-async-example/api/async/start

curl -v -H "Accept: application/vnd.hmrc.1.0+json" -H "Authorization: Bearer cb44fef9-bea5-47bb-9201-f2c789ab1fa6" -H "Cookie: mdtp=x4mPNoF+ZFPfuQk+pN9eJmBuMVX+qNSv/2N8/zR/ax7mczKs/eMQH9yIy7kqxaxtBATTNg2GUgevEI+VwMd56mBk65KSWtZ4yuajszg2IAhkRC+V5KTe;path=/;session=true;secure=false;domain=localhost" http://localhost:8236/api-gateway-async-example/api/async/exampleapi?testId=1234

curl -v -H "Accept: application/vnd.hmrc.1.0+json" -H "Authorization: Bearer cb44fef9-bea5-47bb-9201-f2c789ab1fa6" -H "Cookie: mdtp=kGCJTSF2g6vH5/gpPKwbqBD0gIyyd0H8kBeJ09UQ2UZRtGN7REFi3ZyBDWcpZfbq6DbXuTmtiQWlPdHPpjN/swfIVfupmWkmdzUE02CLAB3BAeUo44S+AiCwLGMtH84lP4U6Wm8Wq2kbFxUbpw0KpCwiZgLlmRxTYMubnAn0KSXIorRvDHi+9TiXrZYm0l4CY5qcx869mrZDCxLVY9CAtZsWoPfAXVaz0Nal0bfutp1bAuY+BlISe4yqnj0QSvPa6dS+VgZuI6f/H3qSm54+fDAZPOEbRjV5/qeKWNoAVcYXRenjQSYQICmjlzunhD+CSQ==;path=/;session=true;secure=false;domain=localhost" http://localhost:8236/api-gateway-async-example/api/async/poll
