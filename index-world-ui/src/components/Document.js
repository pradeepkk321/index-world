import './Document.scss';

const Document = (props) => {

     return (
          <div className="Document">
               <h4 className="document-title">Data</h4>
               {props.document &&
                    <div className="document-data">
                         <pre>{JSON.stringify(props.document, null, 8)}</pre>
                    </div>
               }
          </div>
     );
}

export default Document;