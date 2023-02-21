import { useEffect, useState } from 'react';
import DocumentList from '../components/DocumentList';
import './Collections.scss';

const Collections = () => {

    const [schemaList, setSchemaList] = useState([]);
    const [collectionName, setCollectionName] = useState();

    useEffect(
        () => {
            console.log('Re-Rendering Collections');
            const fetchSchemaList = async () => {
                const response = await fetch(process.env.REACT_APP_API_BASE_URL + '/api/schema/list');
                const data = await response.json();
                console.log('Collection list: ', data);
                setSchemaList(data);
                setCollectionName(data[0]);
            };
            fetchSchemaList();
        }, []
    );

    const updateCurentSchema = (param) => {
        console.log('this is:', param);
        setCollectionName(param);
    };

    return (
        <div className="Collections">
            <div className="collection-col-1">
                <h4 className="collections-title">Collections</h4>
                {schemaList.map(schema => (
                    <div className={collectionName === schema ? "collection-list-item-active" : "collection-list-item"} key={schema}>
                        <p onClick={() => updateCurentSchema(schema)}>{schema}</p>
                        {/* <Link to={`collections/${schema}`}>{schema}</Link> */}
                    </div>
                ))}
            </div>
            <DocumentList schema={collectionName} />
        </div>
    );
}

export default Collections;