import React, { Component } from "react"

export default class ChatInput extends Component {
  state = {
    message: ""
  }

  onChange(e) {
    this.setState({ message: e.target.value })
  }

  sendMessage(e) {
    e.preventDefault()
    this.postData(`localhost:8080`, { message: this.state.message })
      .then(data => console.log(data)) // JSON from `response.json()` call
      .catch(error => console.error(error))
  }

  postData(url = ``, data = {}) {
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

  render() {
    return (
      <div>
        <input
          type="text"
          name="chatinput"
          value={this.state.message}
          placeholder="Enter message here..."
          onChange={e => this.onChange(e)}
          style={{ margin: 0, width: 500, height: 25 }}
        />
        <button
          onClick={e => this.sendMessage(e)}
          style={{ margin: 0, width: 50, height: 26 }}
        >
          Send
        </button>
      </div>
    )
  }
}
