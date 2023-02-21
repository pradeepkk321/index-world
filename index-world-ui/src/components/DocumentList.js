import { useEffect, useState } from "react";
import Document from "./Document";

import './DocumentList.scss';

const DocumentList = (props) => {

    const [pageNo, setPageNo] = useState(1);
    const [pageSize, setPageSize] = useState(10);

    const [searchResponse, setSearchResponse] = useState({ "data": [], metaData: {} });
    const [metadata, setMetadata] = useState({});

    const [activeId, setActiveId] = useState('');
    const [document, setDocument] = useState(null);

    const [searchInput, setSearchInput] = useState(null);
    const [trigger, setTrigger] = useState(false);

    const [collectionName, setCollectionName] = useState('');


    useEffect(
        () => {
            const fetchDocuements = async (currentColectionName) => {

                console.log('Re-Rendering DocumentList');
                // setDocument(null);
                // console.log('Document set to null');
                // setSearchInput(null);

                if (!currentColectionName)
                    return;
                console.log("Current schema: ", currentColectionName)

                // if(collectionName !== currentColectionName) {
                //     setCollectionName(currentColectionName);
                //     setPageNo(1);
                //     console.log("Current collectionName set to ", collectionName);
                //     console.log("Current pageNo set to ", pageNo);
                // }

                var query = null;
                var pageNoNew = null;
                var pageSizeNew = null;

                if (collectionName !== currentColectionName) {
                    setCollectionName(currentColectionName);
                    
                    console.log("Current collectionName set to ", collectionName);
                    console.log("Current pageNo set to ", pageNo);
                    setSearchInput('');
                    setPageNo(1);
                    setPageSize(10);

                    pageSizeNew = 10;
                    pageNoNew = 1;
                    query = '*';
    
                } else {
                    query = searchInput ? searchInput : '*';
                    pageNoNew = pageNo;
                    pageSizeNew = pageSize;
                }

                const response = await fetch(process.env.REACT_APP_API_BASE_URL + '/' + currentColectionName + '/search?query=' + query + '&pageNo=' + pageNoNew + '&pageSize=' + pageSizeNew)
                // const response = await fetch(process.env.REACT_APP_API_BASE_URL + '/' + currentColectionName + '/search?query=' + query );

                const json = await response.json();
                console.log(currentColectionName, 'DocumentList', json);
                setSearchResponse(json);
                console.log("searchResponse: ", searchResponse);

                // metadata = json.metaData;

                // setDocument(searchResponse.data[1]);
                // setActiveId(searchResponse.data[1].id);
                console.log('Calling setActiveDocument');
                if (json && json.data && json.metaData) {
                    setActiveDocument(json.data[0]);
                    setMetadata(json.metaData);
                }

                
            };

            console.log('Selected DocumentList ', props.schema);

            

            fetchDocuements(props.schema);

        }, [props.schema, trigger]
    );

    const setActiveDocument = (doc) => {
        console.log('setActiveDocument:', doc);
        setDocument(doc);
        setActiveId(doc.id);
    };

    const handleChange = (e) => {
        e.preventDefault();
        setSearchInput(e.target.value);
    };

    const handleSearch = (e) => {
        e.preventDefault();
        setPageNo(1);
        setTrigger(!trigger);
    };

    const handleNext = (e) => {
        e.preventDefault();
        if (!metadata.isLastPage) {
            setPageNo(metadata.pageNo + 1);
            setTrigger(!trigger);
        }
    };

    const handlePrev = (e) => {
        e.preventDefault();
        if (!metadata.isFirstPage) {
            setPageNo(metadata.pageNo - 1);
            setTrigger(!trigger);
        }
    };

    const handleFirst = (e) => {
        e.preventDefault();
        if (!metadata.isFirstPage) {
            setPageNo(1);
            setTrigger(!trigger);
        }
    };

    const handleLast = (e) => {
        e.preventDefault();
        if (!metadata.isLastPage) {
            setPageNo(metadata.totalPages);
            setTrigger(!trigger);
        }
    };

    const handlePage = (i) => {
        console.log("handlepage=================>")
        if (i !== metadata.pageNo) {
            setPageNo(i);
            setTrigger(!trigger);
        }
    };

    const handlePageSize = (e) => {
        console.log("handlePageSize=================> ", e.target.value);
        setPageSize(e.target.value);
    };

    return (
        <div className="DocumentList">
            <div className="document-list-search-bar">
                <input className="document-list-search-bar-input"
                    type="search"
                    placeholder="Search here"
                    onChange={handleChange}
                    value={searchInput} />
                <select className="search-select" value={pageSize} onChange={handlePageSize}>
                    <option value="5">5</option>
                    <option value="10">10</option>
                    <option value="20">20</option>
                    <option value="50">50</option>
                </select>
                <button className="search-button" onClick={handleSearch} > Search {props.schema}</button>

                <br />
                {
                    metadata &&
                    <p>Showing {(metadata.pageNo * metadata.pageSize - metadata.pageSize + 1)} to {
                        metadata.pageNo * metadata.pageSize - metadata.pageSize + metadata.currentRecords} records
                        of {metadata.totalRecords}</p>
                }
                <button className={metadata.isFirstPage ? "page-button-disabled" : "page-button"} onClick={handleFirst}>First</button>
                <button className={metadata.isFirstPage ? "page-button-disabled" : "page-button"} onClick={handlePrev}>Prev</button>
                <button className={metadata.isLastPage ? "page-button-disabled" : "page-button"} onClick={handleNext}>Next</button>
                <button className={metadata.isLastPage ? "page-button-disabled" : "page-button"} onClick={handleLast}>Last</button>
                <br />

                {(() => {
                    const arr = [];
                    for (let i = (metadata.pageNo - 3); i < metadata.pageNo + 4 && i <= metadata.totalPages; i++) {
                        if (i > 0) {
                            arr.push(
                                <button className={metadata.pageNo === i ? "page-button-disabled" : "page-button"} onClick={() => handlePage(i)}>{i}</button>
                            );
                        }
                    }
                    return arr;
                })()}

            </div>
            <div className="document-list-col-1">
                <h4 className="document-list-title">Documents</h4>
                { searchResponse.data && 
                <div className="document-panel">
                    <ol>
                        {searchResponse.data.map(doc => (
                            <div className={activeId === doc.id ? "document-list-active" : "document-list"}>
                                <li key={doc.id} onClick={() => setActiveDocument(doc)}>{doc.id}</li>
                            </div>
                        ))}
                    </ol>
                </div> }
            </div>
            {document &&
            <div className="document-list-col-2">
                <Document document={document} />
            </div>}
        </div>
    );
}

export default DocumentList;