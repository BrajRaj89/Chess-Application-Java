package com.nframework.common;

public class Response implements java.io.Serializable
{
private boolean success;
private Object result;
private String exception;
public void setSuccess(boolean success)
{
this.success = success;
}
public boolean getSuccess()
{
return this.success;
}
public void setResult(Object result)
{
this.result = result;
}
public Object getResult()
{
return this.result;
}
public void setException(String exception)
{
this.exception = exception;
}
public String getException()
{
return this.exception;
}
public boolean hasException()
{
return this.success=false;
}
}