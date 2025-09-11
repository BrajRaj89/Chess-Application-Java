package com.nframework.common;

public class Request implements java.io.Serializable
{
private String servicePath;
private Object arguments[];

public Request()
{
this.servicePath="";
this.arguments=null;
}
public void setServicePath(String servicePath)
{
this.servicePath = servicePath;
}
public String getServicePath()
{
return this.servicePath;
}
public void setArguments(Object ...arguments)
{
this.arguments=arguments;
}
public Object[] getArguments()
{
return this.arguments;
}
}