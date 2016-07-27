#!/bin/sh

# TODO...WITH FIRST COMMAND...READ THE RESPONSE AND EXTRACT THE COOKIE!!!

curl -v -H "Accept: application/vnd.hmrc.1.0+json" -H "Authorization: Bearer XlLM91CY3hEHqHlrKX9N0bOvMkwaGE2STDyGXDyx7Z67OwC1U0j1FJy7ZsWvUwz7PODZJ1uN3M4J2gr6NRFLuVXm1oxjDTFdeCb90IZ7COYi8amcTjdOD1yyd9GakraZfyj+BS4KXhpIySvQbZmjEPfLSBF9LAvYlPQ2LeDh2qk1TnU/CoN5cH3wc88qbn82" http://localhost:8238/api/async/start

curl -v -H "Accept: application/vnd.hmrc.1.0+json" -H "Authorization: Bearer XlLM91CY3hEHqHlrKX9N0bOvMkwaGE2STDyGXDyx7Z67OwC1U0j1FJy7ZsWvUwz7PODZJ1uN3M4J2gr6NRFLuVXm1oxjDTFdeCb90IZ7COYi8amcTjdOD1yyd9GakraZfyj+BS4KXhpIySvQbZmjEPfLSBF9LAvYlPQ2LeDh2qk1TnU/CoN5cH3wc88qbn82" -H "Cookie: mdtp=x4mPNoF+ZFPfuQk+pN9eJmBuMVX+qNSv/2N8/zR/ax7mczKs/eMQH9yIy7kqxaxtBATTNg2GUgevEI+VwMd56mBk65KSWtZ4yuajszg2IAhkRC+V5KTe;path=/;session=true;secure=false;domain=localhost" http://localhost:8238/api/async/exampleapi?testId=1234

curl -v -H "Accept: application/vnd.hmrc.1.0+json" -H "Authorization: Bearer XlLM91CY3hEHqHlrKX9N0bOvMkwaGE2STDyGXDyx7Z67OwC1U0j1FJy7ZsWvUwz7PODZJ1uN3M4J2gr6NRFLuVXm1oxjDTFdeCb90IZ7COYi8amcTjdOD1yyd9GakraZfyj+BS4KXhpIySvQbZmjEPfLSBF9LAvYlPQ2LeDh2qk1TnU/CoN5cH3wc88qbn82" -H "Cookie: mdtp=kGCJTSF2g6vH5/gpPKwbqBD0gIyyd0H8kBeJ09UQ2UZRtGN7REFi3ZyBDWcpZfbq6DbXuTmtiQWlPdHPpjN/swfIVfupmWkmdzUE02CLAB3BAeUo44S+AiCwLGMtH84lP4U6Wm8Wq2kbFxUbpw0KpCwiZgLlmRxTYMubnAn0KSXIorRvDHi+9TiXrZYm0l4CY5qcx869mrZDCxLVY9CAtZsWoPfAXVaz0Nal0bfutp1bAuY+BlISe4yqnj0QSvPa6dS+VgZuI6f/H3qSm54+fDAZPOEbRjV5/qeKWNoAVcYXRenjQSYQICmjlzunhD+CSQ==;path=/;session=true;secure=false;domain=localhost" http://localhost:1234/api/async/poll




#===================


curl -v -H "Accept: application/vnd.hmrc.1.0+json" -H "Authorization: Bearer cb44fef9-bea5-47bb-9201-f2c789ab1fa6" http://localhost:8236/api-gateway-async-example/api/async/start

curl -v -H "Accept: application/vnd.hmrc.1.0+json" -H "Authorization: Bearer cb44fef9-bea5-47bb-9201-f2c789ab1fa6" -H "Cookie: mdtp=x4mPNoF+ZFPfuQk+pN9eJmBuMVX+qNSv/2N8/zR/ax7mczKs/eMQH9yIy7kqxaxtBATTNg2GUgevEI+VwMd56mBk65KSWtZ4yuajszg2IAhkRC+V5KTe;path=/;session=true;secure=false;domain=localhost" http://localhost:8236/api-gateway-async-example/api/async/exampleapi?testId=1234

curl -v -H "Accept: application/vnd.hmrc.1.0+json" -H "Authorization: Bearer cb44fef9-bea5-47bb-9201-f2c789ab1fa6" -H "Cookie: mdtp=kGCJTSF2g6vH5/gpPKwbqBD0gIyyd0H8kBeJ09UQ2UZRtGN7REFi3ZyBDWcpZfbq6DbXuTmtiQWlPdHPpjN/swfIVfupmWkmdzUE02CLAB3BAeUo44S+AiCwLGMtH84lP4U6Wm8Wq2kbFxUbpw0KpCwiZgLlmRxTYMubnAn0KSXIorRvDHi+9TiXrZYm0l4CY5qcx869mrZDCxLVY9CAtZsWoPfAXVaz0Nal0bfutp1bAuY+BlISe4yqnj0QSvPa6dS+VgZuI6f/H3qSm54+fDAZPOEbRjV5/qeKWNoAVcYXRenjQSYQICmjlzunhD+CSQ==;path=/;session=true;secure=false;domain=localhost" http://localhost:8236/api-gateway-async-example/api/async/poll
