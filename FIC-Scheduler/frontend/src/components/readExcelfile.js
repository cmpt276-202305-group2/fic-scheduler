import * as XLSX from "xlsx";

async function readExcelFile(file) {
  const reader = new FileReader();

  return new Promise((resolve, reject) => {
    reader.onload = (event) => {
      try {
        const buffer = event.target.result;
        const workbook = XLSX.read(buffer, { type: "array" });
        const worksheet = workbook.Sheets[workbook.SheetNames[0]];
        const rows = XLSX.utils.sheet_to_json(worksheet, { header: 1, defval: "" });

        let noDataFromRow = 0;
        for (let i = rows.length; i > 0; --i) {
          const row = rows[i - 1];
          let noDataFromCol = 0;
          // Start at EOL and walk back until we encounter a nonempty cell
          for (let j = row.length; j > 0; --j) {
            if (row[j - 1] + '') {
              noDataFromCol = j;
              break;
            }
          }
          if (noDataFromCol) {
            noDataFromRow = i;
            break;
          }
        }
        resolve(rows.slice(0, noDataFromRow));
      } catch (error) {
        reject(error);
      }
    };

    reader.onerror = (event) => {
      reject(event.target.error);
    };

    reader.readAsArrayBuffer(file);
  });
}

export default readExcelFile;
