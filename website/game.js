const socket = new WebSocket("ws://localhost:8080")

socket.addEventListener("open", () => {
    console.log("joined")
})

socket.addEventListener("message", ev => {
    console.log('data', ev.data)
})

const start_lobby_button = document.getElementById("startlobby")
start_lobby_button.addEventListener("click", () => {
    socket.send(JSON.stringify({action:"start"}))
})
const roll_dice_button = document.getElementById("rolldice")
roll_dice_button.addEventListener("click", () => {
    socket.send(JSON.stringify({action:"rollDice"}))
})
