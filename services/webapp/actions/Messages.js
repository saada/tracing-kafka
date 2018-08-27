import dayjs from "dayjs"

const chatApiUrl = "http://localhost:8080"

export const postData = (url = "", data = {}) => {
  // Default options are marked with *
  return fetch(url, {
    method: "POST", // *GET, POST, PUT, DELETE, etc.
    mode: "cors", // no-cors, cors, *same-origin
    // cache: "no-cache", // *default, no-cache, reload, force-cache, only-if-cached
    // credentials: "same-origin", // include, same-origin, *omit
    headers: {
      "Content-Type": "application/json",
      Accept: "application/json"
    },
    redirect: "follow", // manual, *follow, error
    referrer: "no-referrer", // no-referrer, *client
    body: JSON.stringify(data) // body data type must match "Content-Type" header
  })
    .then(response => {
      if (response.status > 400) {
        throw new Error("Failed status code: " + response.status)
      }
      return response
    })
    .then(response => response.json()) // parses response to JSON
}

export const sendMessage = (author, room, message) => {
  // const alphabet = "abcdefghijklmnopqrstuvwxyz".split("")

  // return Promise.resolve({
  //   id: alphabet
  //     .map(() => alphabet[Math.floor(Math.random(alphabet.length) * 10)])
  //     .join(""),
  //   date: dayjs().format("YYYY-MM-DD HH:mm:ss"),
  //   author,
  //   message
  // })
  return postData(`${chatApiUrl}/message`, {
    author,
    room,
    message
  })
}

export const getMessages = () =>
  fetch(`${chatApiUrl}/message`).then(response => response.json())
