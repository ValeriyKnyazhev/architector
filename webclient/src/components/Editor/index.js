<<<<<<< HEAD
import React from 'react';
import { Editor, EditorState, ContentState } from 'draft-js';
import './Editor.css';
=======
import React from "react";
import { ContentState, Editor, EditorState } from "draft-js";
import "./Editor.css";
>>>>>>> 96fe4ad1aec59918dd1504001a031b8f13b06db4

export default function CodeEditor({ content }) {
  const [editorState, setEditorState] = React.useState(
    EditorState.createWithContent(ContentState.createFromText(content))
  );

  const editor = React.useRef(null);

  function focusEditor() {
    editor.current.focus();
  }

  React.useEffect(() => {
    focusEditor();
  }, []);

  return (
    <div onClick={focusEditor}>
      <Editor
        ref={editor}
        editorState={editorState}
<<<<<<< HEAD
        blockStyleFn={() => 'leftStyle'}
=======
        blockStyleFn={() => "leftStyle"}
>>>>>>> 96fe4ad1aec59918dd1504001a031b8f13b06db4
        onChange={editorState => setEditorState(editorState)}
        readOnly={true}
      />
    </div>
  );
}
