import React , { useState } from "react";
import Table from '@mui/material/Table';
import TableBody from '@mui/material/TableBody';
import TableCell from '@mui/material/TableCell';
import TableContainer from '@mui/material/TableContainer';
import TableHead from '@mui/material/TableHead';
import TableRow from '@mui/material/TableRow';
import Paper from '@mui/material/Paper';
import Papa from "papaparse";
//import axios from "axios";

function UploadPrefereces({data, setData, coloumnArray, setColoumnArray, values, setValues}) {
    const [ShowErrorMessage, setShowErrorMessage] = useState(false); 
    const handleFileUpload = (e) => {
        const file = e.target.files[0];
        if(file) {
            
            Papa.parse(file, {
                header: true,
                skipEmptyLines: true,
                complete: function(results) {
                    const coloumnArray = [];
                    const valuesArray = [];
    
                    results.data.forEach((d)=> {
                        coloumnArray.push(Object.keys(d));
                        valuesArray.push(Object.values(d));
                    });
                    setData(results.data);
                    setColoumnArray(coloumnArray[0]);
                    setValues(valuesArray);
                    
                    // Below code is for uploading the file to the server
                    // const formData = new FormData();
                    // formData.append("file", file);
                    // axios.post("http://localhost:5000/upload", formData, {
                    //     headers: {
                    //         "Content-Type": "multipart/form-data",
                    //     },
                    // }).then((response) => {
                    //     console.log(response);
                    // }).catch((error) => {
                    //     console.log(error);
                    // });
                }
            })
            setShowErrorMessage(false);
        }
        else {
            setShowErrorMessage(true);
        } 
    }
    return (
        <div>
            <h1 style={{color: "black", fontSize: "40px", margin: "0 10px"}}>Upload Preferences</h1>
            <input
                type="file"
                name='file'
                accept='.csv'
                onChange={handleFileUpload}
                style={{display: "block", margin: "10px auto"}}
            ></input>
            <div>
                {ShowErrorMessage ? (
                    <div style={{color: "red", fontSize: 20}}>Please upload a valid csv file</div>
                ): null}
            </div>
            <br/>
            <Paper sx={{ width: '100%'}}>
            <TableContainer sx={{maxHeight: 700}}>
                <Table  stickyHeader aria-label="sticky table">
                    <TableHead>
                        <TableRow>
                            {coloumnArray.map((coloumn, index) => (
                                <TableCell style = {{border: "1px solid black"}} key = {index}>{coloumn}</TableCell>
                            ))}
                        </TableRow>
                    </TableHead>
                    <TableBody>
                        {values.map((v, index) => (
                            <TableRow key = {index}>
                            {v.map((value, index) => (
                                <TableCell style = {{border: "1px solid "}} key = {index}>{value}</TableCell>
                            ))}
                            </TableRow>
                        ))}
                    </TableBody>
                </Table>
            </TableContainer>
            </Paper>
        </div>
    );
}

export default UploadPrefereces;