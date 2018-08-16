import React, { Component } from "react"
import ReactDOM from "react-dom"
import styled, { injectGlobal } from "styled-components"
import ChatRoom from "./components/ChatRoom"

injectGlobal`
  html {
    background-color: black;
    color: cyan;
    font-size: 20px;
  }

  * {
    margin: 6px;
  }
`

const Page = styled.div``

const Header = styled.div`
  font-size: 5em;
`

class App extends Component {
  state = {
    user: "guest-" + Math.floor(Math.random() * 1000),
    room: {
      name: "lobby",
      messages: [
        {
          id: "a",
          date: "2018-08-16T08:00:31-04:00",
          author: "Ralph",
          message: "Hey, there!"
        },
        {
          id: "b",
          date: "2018-08-16T08:02:31-04:00",
          author: "Johnny",
          message: "Hello!",
          image: "https://media.giphy.com/media/l3fQhqrUsJUWs7b0c/giphy.gif"
        }
      ]
    },
    chatInput: "Sample input 😊"
  }

  userInputChange(e) {
    this.setState({ user: e.target.value })
  }

  render() {
    return (
      <Page>
        <Header>Chat</Header>
        <label>User</label>
        <input
          type="text"
          name="user"
          value={this.state.user}
          onChange={e => this.userInputChange(e)}
        />
        <ChatRoom room={this.state.room} messages={this.state.messages} />
      </Page>
    )
  }
}

ReactDOM.render(<App />, document.getElementById("app"))
