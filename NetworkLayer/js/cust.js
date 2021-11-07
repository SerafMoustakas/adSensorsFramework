(function(exports) {
  var slice = [].slice;
  var toString = {}.toString;

  function HookerOverride(v) { this.value = v; }
  function HookerPreempt(v) { this.value = v; }
  function HookerFilter(c, a) { this.context = c; this.args = a; }


  exports.override = function(value) {
    return new HookerOverride(value);
  };


  exports.preempt = function(value) {
    return new HookerPreempt(value);
  };


  exports.filter = function(context, args) {
    return new HookerFilter(context, args);
  };

  function forMethods(obj, props, callback) {
    var prop;
    if (typeof props === "string") {
      props = [props];
    } else if (props == null) {
      props = [];
      for (prop in obj) {
        if (obj.hasOwnProperty(prop)) {
          props.push(prop);
        }
      }
    }
    var i = props.length;
    while (i--) {
      if (toString.call(obj[props[i]]) !== "[object Function]" ||
        callback(obj, props[i]) === false) {
        props.splice(i, 1);
      }
    }
    return props;
  }

  exports.hook = function(obj, props, options) {
    if (options == null) {
      options = props;
      props = null;
    }
    if (typeof options === "function") {
      options = {pre: options};
    }

    return forMethods(obj, props, function(obj, prop) {
      var orig = obj[prop];
       
      function hooked() {
          
        var result, origResult, tmp;
        var args = slice.call(arguments);
        
        if (options.passName) {
          args.unshift(prop);
        }

        if (options.pre) {
          result = options.pre.apply(this, args);
        }

        if (result instanceof HookerFilter) {
          origResult = result = orig.apply(result.context, result.args);
        } else if (result instanceof HookerPreempt) {
          origResult = result = result.value;
        } else {
          origResult = orig.apply(this, arguments);
	  try{
	    //console.log('NetLog: '+"Hook==> Class: "+ obj +" FunctionName :"+ JSON.stringify(orig.name) + "\n");
	  }
	  catch(e){
	  console.log('NetLog: '+"HOOKINGSCRIPTERROR: " + e);
	  }
        
          result = result instanceof HookerOverride ? result.value : origResult;
        }

        if (options.post) {
         
          tmp = options.post.apply(this, [origResult].concat(args));
          if (tmp instanceof HookerOverride) {
            result = tmp.value;
          }
        }

        
        if (options.once) {
          exports.unhook(obj, prop);
        }

       
        return result;
      }
      
      obj[prop] = hooked;
      if (obj[prop] !== hooked) { return false; }
      obj[prop]._orig = orig;
    });
  };

  exports.orig = function(obj, prop) {
    return obj[prop]._orig;
  };

  exports.unhook = function(obj, props) {
    return forMethods(obj, props, function(obj, prop) {
      var orig = exports.orig(obj, prop);
      if (!orig) { return false; }
      obj[prop] = orig;
    });
  };
}(typeof exports === "object" && exports || this));


function getScript() {
  var e = new Error();
  console.log('NetLog: '+e.stack);
  var trace = e.stack.split("\n");
  console.log('NetLog: '+"trace done");
  url = trace[3].split("@");
  console.log('NetLog: '+url);
  ret = url[0].split(":");
  console.log('NetLog: '+"ret done");
  ret2 = ret.slice(0, ret.length-2);
  console.log('NetLog: '+"ret2 done");
  ret3 = ret2.join(":");
  return(ret3);
}


window.addEventListener("load", function(event) {
    console.log('NetLog: '+"FULLYLOADED_new");
	function addXMLRequestCallback(callback){
    var oldSend, i;
    if( XMLHttpRequest.callbacks ) {
        XMLHttpRequest.callbacks.push( callback );
    } else {        
        XMLHttpRequest.callbacks = [callback];
        oldSend = XMLHttpRequest.prototype.send;
        XMLHttpRequest.prototype.send = function(){
            for( i = 0; i < XMLHttpRequest.callbacks.length; i++ ) {
                XMLHttpRequest.callbacks[i]( this );
            }
            oldSend.apply(this, arguments);
        }
    }
}

(function() {
    var origOpen = XMLHttpRequest.prototype.open;
    XMLHttpRequest.prototype.open = function() {
        console.log('NetLog: '+'request started!');
        this.addEventListener('load', function() {
            console.log('NetLog: '+'request completed!');
            console.log('NetLog: '+this.readyState); 
            console.log('NetLog: '+"XMLDATARESPONSE;"+this.responseText+ ";"); 
        });
        origOpen.apply(this, arguments);
    };
})();


// e.g.
addXMLRequestCallback( function( xhr ) {
     console.log('NetLog: '+"FWEBAPILOGP;XMLSEND;"+"\n");
    console.log('NetLog: '+"FWEBAPILOGP;XMLSEND;"+ String(xhr.responseText)+ ";" ); 
});
addXMLRequestCallback( function( xhr ) {
	console.log('NetLog: '+"FWEBAPILOGP;XMLSEND;"+"\n");	    
	console.log('NetLog: '+"FWEBAPILOGP;XMLSEND;"+ String(xhr)+ ";" ); 
});
  });

// ==========================================   MOBILE SPECIFIC CALLS  ============================================= //

hook(XMLHttpRequest,"open",function() { 
	//alert("getCurrentPosition called"); 
	console.log('NetLog: '+"MWEBAPILOGP;XMLSEND;"+"\n");
});

hook(navigator.geolocation,"getCurrentPosition",function() { 
	//alert("getCurrentPosition called"); 
	console.log('NetLog: '+"MWEBAPILOGP;getCurrentPosition;"+ window.location.host + ";" + getScript() +";" + navigator.userAgent + "\n");
});

hook(navigator.geolocation,"watchPosition",function() { 
	//alert("watchPosition called"); 
	console.log('NetLog: '+"MWEBAPILOGP;watchPosition;"+ window.location.host + ";" + getScript() + ";" + navigator.userAgent + "\n");
});

hook(navigator,"getBattery",function() { // not supported by firefox
	//alert("getBattery called"); 
	console.log('NetLog: '+"MWEBAPILOG;getBattery;"+ window.location.host + ";" + getScript() + ";" +navigator.userAgent + "\n");
});

hook(navigator.mediaDevices,"getUserMedia",function() { 
	console.log('NetLog: '+"MWEBAPILOGP;getUserMedia;"+ window.location.host + ";" + getScript() + ";" +navigator.userAgent + "\n");
});

hook(navigator,"getUserMedia",function() { 
	console.log('NetLog: '+"MWEBAPILOGP;getUserMediaDep;"+ window.location.host + ";" + getScript() + ";" +navigator.userAgent + "\n");
});

hook(navigator,"vibrate", function() {
	console.log('NetLog: '+"MWEBAPILOGP;vibrate;"+ window.location.host + ";" + getScript() + ";" +navigator.userAgent + "\n");
});

hook(window,"addEventListener",function() {
	console.log('NetLog: '+"MWEBAPILOG_new "+arguments[0]);
	var array = ['deviceproximity','userproximity','devicelight', 'deviceorientation', 'deviceorientationabsolute', 'devicemotion','orientationchange'];
	if (array.indexOf(arguments[0]) >= 0 ){
		console.log('NetLog: '+"MWEBAPILOG_new;"+ arguments[0] +";"+ window.location.host + ";" + getScript() + ";" +navigator.userAgent + "\n");
	}
});

hook(screen.orientation,"addEventListener",function() {
	console.log('NetLog: '+"MWEBAPILOG_new "+arguments[0]);
	var array = ['change'];
	if (array.indexOf(arguments[0]) >= 0 ){
		console.log('NetLog: '+"MWEBAPILOG_new;"+ arguments[0] +";"+ window.location.host + ";" + getScript() + ";" +navigator.userAgent + "\n");
	}
});


hook(screen.orientation,"lock", function() {
	console.log('NetLog: '+"MWEBAPILOG;orientation.lock;"+ window.location.host + ";" + getScript() + ";" +navigator.userAgent + "\n");
});


hook(screen,"mozLockOrientation", function() {
	console.log('NetLog: '+"MWEBAPILOG;mozLockOrientation;"+ window.location.host + ";" + getScript() + ";" +navigator.userAgent + "\n");
});

hook(navigator,"lockOrientation", function() {
	console.log('NetLog: '+"MWEBAPILOG;lockOrientation;"+ window.location.host + ";" + getScript() + ";" +navigator.userAgent + "\n");
});


// ======================================= 	NON MOBILE SPECIFIC CALLS      ======================================== //

//	-----	CANVAS	-----	//

hook(document, "createElement", function(){
	var array = ['canvas', 'webgl'];
	if (array.indexOf(arguments[0].toLowerCase()) >= 0) {
	console.log('NetLog: '+"FWEBAPILOG;" + arguments[0].toLowerCase() + ";"+ window.location.host + ";" + getScript() + ";" +navigator.userAgent + "\n");
	}
});


hook(HTMLCanvasElement.prototype, "getContext", function(){
	var array = ['2d', 'webgl'];
	if (array.indexOf(arguments[0].toLowerCase()) >= 0) {
	console.log('NetLog: '+"FWEBAPILOG;HTMLCanvasElement.getContext("+arguments[0].toLowerCase()+");"+ window.location.host + ";" + getScript() + ";" +navigator.userAgent + "\n");
	}
});


hook(CanvasRenderingContext2D.prototype, "fillRect", function(){
	console.log('NetLog: '+"FWEBAPILOG;CanvasRenderingContext2D.fillRect;"+ window.location.host + ";" + getScript() + ";" +navigator.userAgent + "\n");
});

hook(CanvasRenderingContext2D.prototype, "fillText", function(){
	console.log('NetLog: '+"FWEBAPILOG;CanvasRenderingContext2D.fillText;"+ window.location.host + ";" + getScript() + ";" +navigator.userAgent + "\n");
});

hook(HTMLCanvasElement.prototype, "toDataURL", function(){
	console.log('NetLog: '+"FWEBAPILOG;HTMLCanvasElement.toDataURL;"+ window.location.host + ";" + getScript() + ";" +navigator.userAgent + "\n");
});

//	-----	WEBGL   -----	 //

const arr = Object.getOwnPropertyNames(WebGLRenderingContext.prototype).filter( x => {
	const t = Object.getOwnPropertyDescriptor(WebGLRenderingContext.prototype, x);
 	return !t.get && typeof WebGLRenderingContext.prototype[x] === 'function'
})

arr.forEach(function(i){
hook(WebGLRenderingContext.prototype, i, function(){
	console.log('NetLog: '+"FWEBAPILOG;WebGLRenderingContext."+i+";"+ window.location.host + ";" + getScript() + ";" +navigator.userAgent + "\n");
});
})

//	-----	TIMEZONE  -----	 //

hook(Date.prototype, "getTimezoneOffset", function(){
	console.log('NetLog: '+"FWEBAPILOG;getTimezoneOffset;"+ window.location.host + ";" + getScript() + ";" +navigator.userAgent + "\n");
});

//	-----	LOCALSTORAGE  -----	 //

hook(Storage.prototype, "setItem", function(){
	console.log('NetLog: '+"FWEBAPILOG;Storage.setItem;"+ window.location.host + ";" + getScript() + ";" +navigator.userAgent + "\n");
});


hook(Storage.prototype, "getItem", function(){
	console.log('NetLog: '+"FWEBAPILOG;Storage.getItem;"+ window.location.host + ";" + getScript() + ";" +navigator.userAgent + "\n");
});

hook(Storage.prototype, "key", function(){
	console.log('NetLog: '+"FWEBAPILOG;Storage.key;"+ window.location.host + ";" + getScript() + ";" +navigator.userAgent + "\n");
});

hook(Storage.prototype, "removeItem", function(){
	console.log('NetLog: '+"FWEBAPILOG;Storage.removeItem;"+ window.location.host + ";" + getScript() + ";" +navigator.userAgent + "\n");
});

hook(Storage.prototype, "clear", function(){
	console.log('NetLog: '+"FWEBAPILOG;Storage.clear;"+ window.location.host + ";" + getScript() + ";" +navigator.userAgent + "\n");
});


hook(navigator, "share", function(){
	console.log('NetLog: '+"FWEBAPILOG;navigator.share;"+ window.location.host + ";" + getScript() + ";" +navigator.userAgent + "\n");
});

// --- AUDIO

hook(window.AudioContext.prototype, "close", function(){
	console.log('NetLog: '+"FWEBAPILOG;AudioContext.close;"+ window.location.host + ";" + getScript() + ";" +navigator.userAgent + "\n");
});


hook(window.AudioContext.prototype, "createMediaElementSource", function(){
	console.log('NetLog: '+"FWEBAPILOG;AudioContext.createMediaElementSource;"+ window.location.host + ";" + getScript() + ";" +navigator.userAgent + "\n");
});


hook(window.AudioContext.prototype, "createMediaStreamSource", function(){
	console.log('NetLog: '+"FWEBAPILOG;AudioContext.createMediaStreamSource;"+ window.location.host + ";" + getScript() + ";" +navigator.userAgent + "\n");
});

hook(window.AudioContext.prototype, "createMediaStreamDestination", function(){
	console.log('NetLog: '+"FWEBAPILOG;AudioContext.createMediaStreamDestination;"+ window.location.host + ";" + getScript() + ";" +navigator.userAgent + "\n");
});

hook(window.AudioContext.prototype, "createMediaStreamTrackSource", function(){
	console.log('NetLog: '+"FWEBAPILOG;AudioContext.createMediaStreamTrackSource;"+ window.location.host + ";" + getScript() + ";" +navigator.userAgent + "\n");
});

hook(window.AudioContext.prototype, "getOutputTimestamp", function(){
	console.log('NetLog: '+"FWEBAPILOG;AudioContext.getOutputTimestamp;"+ window.location.host + ";" + getScript() + ";" +navigator.userAgent + "\n");
});

hook(window.AudioContext.prototype, "resume", function(){
	console.log('NetLog: '+"FWEBAPILOG;AudioContext.resume;"+ window.location.host + ";" + getScript() + ";" +navigator.userAgent + "\n");
});

hook(window.AudioContext.prototype, "suspend", function(){
	console.log('NetLog: '+"FWEBAPILOG;AudioContext.suspend;"+ window.location.host + ";" + getScript() + ";" +navigator.userAgent + "\n");
});


hook(window.OfflineAudioContext.prototype, "suspend", function(){
	console.log('NetLog: '+"FWEBAPILOG;OfflineAudioContext.suspend;"+ window.location.host + ";" + getScript() + ";" +navigator.userAgent + "\n");
});

hook(window.OfflineAudioContext.prototype, "resume", function(){
	console.log('NetLog: '+"FWEBAPILOG;OfflineAudioContext.resume;"+ window.location.host + ";" + getScript() + ";" +navigator.userAgent + "\n");
});

hook(window.OfflineAudioContext.prototype, "startRendering", function(){
	console.log('NetLog: '+"FWEBAPILOG;OfflineAudioContext.startRendering;"+ window.location.host + ";" + getScript() + ";" +navigator.userAgent + "\n");
});

const arr2 = Object.getOwnPropertyNames(window.BaseAudioContext.prototype).filter( x => {
	const t = Object.getOwnPropertyDescriptor(window.BaseAudioContext.prototype, x);
 	return !t.get && typeof window.BaseAudioContext.prototype[x] === 'function'
})

arr2.forEach(function(k){
hook(window.BaseAudioContext.prototype, k, function(){
	console.log('NetLog: '+"FWEBAPILOG;BaseAudioContext."+k+";"+ window.location.host + ";" + getScript() + ";" +navigator.userAgent + "\n");
});
})

//xxxxxxxxxx BLOCK CONTEXT MENUS + LOGGING xxxxxxxxxxxxxxxx//
document.oncontextmenu = function() {
    return false;
}

console.log('NetLog: '+"--- Script 1 Injected in " + window.location + " ---");




