class WebSocketController {

    constructor() {
        this._onConnected = this._onConnected.bind(this);
    }

    _onConnected(frame) {
        this.setConnected(true);
        console.log('Connected: ' + frame);
        fetch("/api/auth/me").then((response) => response.json()).then((res)=>{
            console.log(res);
            this.stompClient.subscribe(`/topic/feed.${res.id}`, this.showMessage);
        })


    }

    setConnected(connected) {
        document.getElementById('connect').disabled = connected;
        document.getElementById('disconnect').disabled = !connected;
        document.getElementById('mural').style.visibility = connected ? 'visible' : 'hidden';
        document.getElementById('response').innerHTML = '';
    }

    connect() {
        var socket = new SockJS('/websocket');
        this.stompClient = Stomp.over(socket);
        this.stompClient.connect({}, this._onConnected);
    }

    disconnect() {
        if(this.stompClient != null) {
            this.stompClient.disconnect();
        }
        this.setConnected(false);
        console.log("Disconnected");
    }

    sendMessage() {
        var message = document.getElementById('text').value;
        this.stompClient.send("/app/message", {}, message);
    }

    showMessage(message) {
        var response = document.getElementById('response');
        var p = document.createElement('p');
        p.style.wordWrap = 'break-word';
        p.appendChild(document.createTextNode(new Date() + message.body));
        response.insertBefore(p,response.firstChild);
    }

}
var webSocket = new WebSocketController();
