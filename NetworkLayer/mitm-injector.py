from bs4 import BeautifulSoup
from mitmproxy import ctx, http

f1 = open("testing_app", "r") #a file that contains the name of the app that we test
running_app=f1.read()
running_app=running_app.replace("\n", "")
print ("\n\n\n||||||||||||||||||||||||"+running_app+"||||||||||||||||||||||||")

f2 = open(running_app+".txt", "a+")
with open("js/cust.js") as f:
    sc=f.read()
    ctx.log.info("\n\n\nScript Ready\n\n\n")
with open("js/cust2.js") as fc2:
    sc2=fc2.read()
    ctx.log.info("\n\n\nScript Ready\n\n\n")
		
def response(flow: http.HTTPFlow) -> None:
    html = BeautifulSoup(flow.response.content, "html.parser")   
    ctx.log.info("FLOW: "+ str(flow))
    #SAVE FLOW
    #file.write(str(flow) + "\n")
    #file.close()
    #END
    ctx.log.info("\n\n\nInjecting...\n\n\n")
    if "<head>" in str(html) and ("text/javascript" in flow.response.headers["content-type"]):
        scr = html.new_tag("script", type="application/javascript")
        scr.string=sc2
        new_try=str(scr).replace("\n","")
        new_try=bytes("<body>"+new_try,"utf-8")
        finding=bytes("<body>","utf-8")
        x=flow.response.content.replace(finding,new_try)
        ctx.log.info("\n\n\nbefore......\n\n\n"+str(flow.response.content)+"\n\n\n")
        flow.response.content = x	
        ctx.log.info("\n\n\nScript injected ON JAVASCRIPT\n\n\n"+str(flow.response.content)+"\n\n\n")

    #Save flows
    name=str(flow.request.pretty_url)
    f2.write("---###---###---###---###---###"+name+"---###---###---###---###---###")
    f2.write(str(html.encode('ascii', 'ignore')))
    if html.body and ("text/html" in flow.response.headers["content-type"]):     # inject only for HTML resources
        if "Content-Security-Policy" in flow.response.headers:
            del flow.response.headers["Content-Security-Policy"]
        scr = html.new_tag("script", type="application/javascript")
        scr.string=sc
        html.body.insert(0, scr)
        flow.response.content = str(html).encode("utf8")
        ctx.log.info("\n\n\nScript injected\n\n\n")
    f2.flush()


