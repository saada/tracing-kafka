import dayjs from "dayjs"

export const postData = (url = ``, data = {}) => {
  // Default options are marked with *
  return fetch(url, {
    method: "POST", // *GET, POST, PUT, DELETE, etc.
    mode: "cors", // no-cors, cors, *same-origin
    cache: "no-cache", // *default, no-cache, reload, force-cache, only-if-cached
    credentials: "same-origin", // include, same-origin, *omit
    headers: {
      "Content-Type": "application/json; charset=utf-8"
      // "Content-Type": "application/x-www-form-urlencoded",
    },
    redirect: "follow", // manual, *follow, error
    referrer: "no-referrer", // no-referrer, *client
    body: JSON.stringify(data) // body data type must match "Content-Type" header
  }).then(response => response.json()) // parses response to JSON
}

export const sendMessage = (user, room, message) => {
  const alphabet = "abcdefghijklmnopqrstuvwxyz".split("")

  return Promise.resolve({
    id: alphabet
      .map(() => alphabet[Math.floor(Math.random(alphabet.length) * 10)])
      .join(""),
    date: dayjs().format("YYYY-MM-DD HH:mm:ss"),
    author: user,
    message
  })
  return this.postData(`localhost:8080`, { message })
    .then(data => console.log(data)) // JSON from `response.json()` call
    .catch(error => console.error(error))
}

export const getMessages = () => fetch(url).then(response => response.json())
