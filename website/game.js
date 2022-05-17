const socket = new WebSocket("ws://localhost:8080")

socket.addEventListener("open", () => {
    console.log("joined")
    const start_lobby_button = document.getElementById("startlobby")
    start_lobby_button.addEventListener("click", () => {
        socket.send(JSON.stringify({action:"start"}))
    })
    const roll_dice_button = document.getElementById("rolldice")
    roll_dice_button.addEventListener("click", () => {
        socket.send(JSON.stringify({action:"rollDice"}))
    })
})

socket.addEventListener("message", ev => {
    console.log('data', ev.data)
})

const svg_elem = document.getElementById("svgbuddy")
const use = svg_elem.getElementsByTagName("use")[0]
use.remove()
console.log(use)
// generate all the hexagons : )
for (let x = 0; x < 10; ++x) {
    for (let y = 0; y < 2; ++y) {
        let elem = use.cloneNode()
        elem.setAttribute("x", (x - y) * 10 + (y * 5))
        elem.setAttribute("y", y * 7.5)
        svg_elem.appendChild(elem)
    }
}

