import React, { Component } from "react";
import {
  BrowserRouter as Router,
  Route,
  Switch,
  Redirect
} from "react-router-dom";
import "./App.css";

const Projects = () => <div> Projects </div>;
const Files = () => <div> Files </div>;

class App extends Component {
  render() {
    return <div className="App">
        <Router>
            <Switch>
                <Route path="/projects" component={Projects} />
                <Route path="/files" component={Files} />
                <Redirect exact from="/" to="/projects" />
            </Switch>
        </Router>
    </div>;
  }
}

export default App;
