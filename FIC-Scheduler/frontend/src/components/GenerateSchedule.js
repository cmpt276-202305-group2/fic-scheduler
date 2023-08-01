import React, { useState } from "react";
import Button from "@mui/material/Button";
import ScheduleTable from "../components/ScheduleTable";

function GenerateSchedule() {
    const [showSchedule, setShowSchedule] = useState(false);
    const [tableKey, setTableKey] = useState(0);

    const handleShowSchedule = () => {
        setShowSchedule(true);
        setTableKey(prevKey => prevKey + 1);
    };

    return (
        <div>
            <Button
                variant="contained"
                color="primary"
                sx={{
                    color: "white",
                    backgroundColor: "#417A1A",
                    "&:hover": { backgroundColor: "#417A1A" },
                }}
                onClick={handleShowSchedule}
                style={{ marginBottom: 10 }}
            >
                Generate Schedule
            </Button>
            {showSchedule && <ScheduleTable key={tableKey} />}
        </div>
    );
}

export default GenerateSchedule;