import React , { useState } from "react";
import Table from '@mui/material/Table';
import TableBody from '@mui/material/TableBody';
import TableCell from '@mui/material/TableCell';
import TableContainer from '@mui/material/TableContainer';
import TableHead from '@mui/material/TableHead';
import TableRow from '@mui/material/TableRow';
import Paper from '@mui/material/Paper';
import Papa from "papaparse";

function UploadPrefereces({data, coloumnArray, values, setValues, setdata, setcoloumnArray}) {
    const [ShowErrorMessage, setShowErrorMessage] = useState(false); 
    // const [data, setdata] = useState([]);
    // const [coloumnArray, setcoloumnArray] = useState([]);
    // const [values, setValues] = useState([]);
    const handleFileUpload = (e) => {
        const file = e.target.files[0];
        if(file) {
            
            Papa.parse(file, {
                header: true,
                skipEmptyLines: true,
                complete: function(results) {
                    const coloumnArray = [];
                    const valuesArray = [];
    
                    results.data.map((d)=> {
                        coloumnArray.push(Object.keys(d));
                        valuesArray.push(Object.values(d));
                    });
                    setdata(results.data);
                    setcoloumnArray(coloumnArray[0]);
                    setValues(valuesArray);
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