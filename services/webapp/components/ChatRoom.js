import React, { Component } from "react"
import ChatInput from "./ChatInput"

import dayjs from "dayjs"
import relativeTime from "dayjs/plugin/relativeTime"
dayjs.extend(relativeTime)

import styled from "styled-components"
const TimeAgo = styled.label`
  font-size: 0.5em;
  color: gold;
`

export default class ChatRoom extends Component {
  render() {
    return (
      <div>
        <h2>{this.props.room.name}</h2>
        <ul>
          {this.props.room.messages.map(message => (
            <li key={message.id}>
              {message.author} - {message.message}
              <TimeAgo>{dayjs(message.date).fromNow()}</TimeAgo>
              <br />
              {message.image ? <img src={message.image} height="100" /> : null}
            </li>
          ))}
        </ul>
        <ChatInput room={this.props.room.name} />
      </div>
    )
  }
}
