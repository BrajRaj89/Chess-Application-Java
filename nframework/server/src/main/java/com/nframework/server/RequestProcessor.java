package com.nframework.server;
import com.nframework.common.*;
import java.io.*;
import java.net.*;
import java.nio.charset.*;
import java.lang.reflect.*;
class RequestProcessor extends Thread
{
private NFrameworkServer server;
private Socket socket;
RequestProcessor(NFrameworkServer server,Socket socket)
{
this.server = server;
this.socket =socket;
start();
}
public void run()
{
try
{
InputStream is = socket.getInputStream();
OutputStream os = socket.getOutputStream();
int bytesToReceive  =1024;
byte header[] = new byte[1024];
byte tmp[] = new byte[1024];
int bytesReadCount;
int i,j,k;
i=0;
j=0;
while(j<bytesToReceive)
{
bytesReadCount = is.read(tmp);
for(k=0; k<bytesToReceive; k++)
{
header[i] = tmp[k];
i++;
}
j = j+bytesReadCount;
}
int requestLength =0;
j=1023;
i=1;
while(j>0)
{
requestLength = requestLength+(header[j]*i);
i = i*10;
j--;
}
byte ack[] = new byte[1];
ack[0] =1;
os.write(ack,0,1);
os.flush();
byte request[] = new byte[requestLength];
bytesToReceive = requestLength;
i=0;
j=0;
while(j<bytesToReceive)
{
bytesReadCount = is.read(tmp);
for(k=0; k<bytesReadCount; k++)
{
request[i] = tmp[k];
i++;
}
j = j+bytesReadCount;
}
String requestJSONString = new String(request,StandardCharsets.UTF_8);
Request requestObject  = JSONUtil.fromJSON(requestJSONString,Request.class);
String servicePath = requestObject.getServicePath();
TCPService tcpService = this.server.getTCPService(servicePath);
Response responseObject = new Response();
if(tcpService==null)
{
responseObject.setSuccess(false);
responseObject.setResult(null);
responseObject.setException("Invalid path: "+servicePath);
}
else
{
Class<?> c = tcpService.c;
Method method = tcpService.method;
try
{
Object serviceObject = c.getDeclaredConstructor().newInstance();
Object result = method.invoke(serviceObject,requestObject.getArguments());
responseObject.setSuccess(true);
responseObject.setResult(result);
responseObject.setException(null);
}catch(InstantiationException instantiationException)
{
responseObject.setSuccess(false);
responseObject.setResult(null);
responseObject.setException("unable to create object to service class associated with the path :"+servicePath);
}catch(IllegalAccessException illegalAccessException)
{
responseObject.setSuccess(false);
responseObject.setResult(null);
responseObject.setException("unable to create object to service class associated with the path :"+servicePath);
}catch(InvocationTargetException invocationTargetException)
{
Throwable cause = invocationTargetException.getCause();
responseObject.setSuccess(false);
responseObject.setResult(null);
System.out.println(cause.getMessage());
responseObject.setException(cause.getMessage());
}
 String responseString = JSONUtil.toJSON(responseObject);
byte objectBytes[] = responseString.getBytes(StandardCharsets.UTF_8);
int responseLength = objectBytes.length;
int x;
i =1023;
x = responseLength;
while (x > 0) {
    header[i] = (byte) ('0' + (x % 10)); 
    x = x / 10;
    i--;
}
// String headerstring = new String(header,StandardCharsets.UTF_8);
os.write(header,0,1024);
os.flush();
while(true)
{
bytesReadCount = is.read(ack);
if(bytesReadCount==-1) continue;
break;
}
int bytesToSend = responseLength;
int chunkSize =1024;
j=0;
while(j<bytesToSend)
{
if((bytesToSend-j)<chunkSize) chunkSize = bytesToSend-j;
os.write(objectBytes,j,chunkSize);
os.flush();
j = j+chunkSize;
}
while(true)
{
bytesReadCount = is.read(ack);
if(bytesReadCount ==-1) continue;
break;
}
socket.close();
}
}catch(Exception e)
{
System.out.println(e);
}
}
}