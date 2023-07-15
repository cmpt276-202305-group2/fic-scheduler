import ExcelJS from "exceljs";

async function readExcelFile(filePath) {
  const workbook = new ExcelJS.Workbook();
  await workbook.xlsx.readFile(filePath);

  const worksheet = workbook.getWorksheet(1);
  const rows = worksheet.getRows();

  const data = rows.map((row) => row.values);
  return data;
}

export default readExcelFile;
