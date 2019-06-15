import React, { PureComponent } from 'react';
import { ContentState, Editor, EditorState } from 'draft-js';
import './CodeEditor.css';

export default class CodeEditor extends PureComponent {
  constructor(props) {
    super(props);
    this.state = {
      editorState: EditorState.createWithContent(ContentState.createFromText(props.content)),
      conflictResolved: true
    };
    this.onChange = editorState => {
      const contentState = editorState.getCurrentContent().getPlainText();
      this.props.onUpdateContent(contentState);
      this.setState({ editorState });
    };
    this.setEditor = editor => {
      this.editor = editor;
    };
    this.focusEditor = () => {
      if (this.editor) {
        this.editor.focus();
      }
    };
  }

  componentDidMount() {
    this.focusEditor();
  }

  componentDidUpdate(prevProps, prevState) {
    if (this.props.conflictResolved && prevState.conflictResolved) {
      this.setState({
        editorState: EditorState.createWithContent(ContentState.createFromText(this.props.content)),
        conflictResolved: false
      });
    }
  }

  render() {
    return (
      <div style={styles.editor} onClick={this.focusEditor}>
        <Editor
          ref={this.setEditor}
          editorState={this.state.editorState}
          blockStyleFn={() => 'leftStyle'}
          onChange={this.onChange}
          readOnly={this.props.readOnly}
        />
      </div>
    );
  }
}

const styles = {
  editor: {
    border: '1px solid gray',
    minHeight: '2em'
  }
};
