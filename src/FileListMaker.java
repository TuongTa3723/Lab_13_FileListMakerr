import java.io.BufferedReader;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.FileNotFoundException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Scanner;

public class FileListMaker
{
    // program state
    private static ArrayList<String> list = new ArrayList<>();
    private static boolean needsToBeSaved = false;  // dirty flag
    private static String currentFileName = null;   // e.g. "Groceries.txt"

    public static void main(String[] args)
    {
        Scanner in = new Scanner(System.in);
        boolean done = false;

        while (!done)
        {
            displayMenu();
            String choice = SafeInput.getNonZeroLenString(in, "Enter option").toUpperCase();
            char option = choice.charAt(0);

            switch (option)
            {
                case 'A': // Add
                    addItem(in);
                    break;

                case 'D': // Delete
                    deleteItem(in);
                    break;

                case 'I': // Insert
                    insertItem(in);
                    break;

                case 'M': // Move
                    moveItem(in);
                    break;

                case 'V': // View
                    viewList();
                    break;

                case 'C': // Clear
                    clearList(in);
                    break;

                case 'O': // Open
                    try
                    {
                        handleOpen(in);
                    }
                    catch (IOException e)
                    {
                        System.out.println("Error opening file: " + e.getMessage());
                    }
                    break;

                case 'S': // Save
                    try
                    {
                        handleSave(in);
                    }
                    catch (IOException e)
                    {
                        System.out.println("Error saving file: " + e.getMessage());
                    }
                    break;

                case 'Q': // Quit
                    done = handleQuit(in);
                    break;

                default:
                    System.out.println("Invalid option. Please try again.");
            }
        }

        System.out.println("Goodbye!");
    }


    private static void displayMenu()
    {
        System.out.println("\nFile List Maker Menu");
        System.out.println("A – Add an item");
        System.out.println("D – Delete an item");
        System.out.println("I – Insert an item");
        System.out.println("M – Move an item");
        System.out.println("V – View the list");
        System.out.println("O – Open a list file from disk");
        System.out.println("S – Save the current list to disk");
        System.out.println("C – Clear the current list");
        System.out.println("Q – Quit");
    }



    private static void viewList()
    {
        System.out.println("\nCurrent List:");
        if (list.isEmpty())
        {
            System.out.println("[The list is empty]");
        }
        else
        {
            for (int i = 0; i < list.size(); i++)
            {
                System.out.printf("%3d: %s%n", i + 1, list.get(i));
            }
        }
    }

    private static void addItem(Scanner in)
    {
        String item = SafeInput.getNonZeroLenString(in, "Enter item to add");
        list.add(item);
        needsToBeSaved = true;
    }

    private static void deleteItem(Scanner in)
    {
        if (list.isEmpty())
        {
            System.out.println("List is empty. Nothing to delete.");
            return;
        }

        viewList();
        int index = SafeInput.getRangedInt(in, "Enter item number to delete", 1, list.size());
        list.remove(index - 1);
        needsToBeSaved = true;
    }

    private static void insertItem(Scanner in)
    {
        String item = SafeInput.getNonZeroLenString(in, "Enter item to insert");

        int position;
        if (list.isEmpty())
        {
            position = 1;
        }
        else
        {
            viewList();
            position = SafeInput.getRangedInt(in, "Enter position to insert at", 1, list.size() + 1);
        }

        list.add(position - 1, item);
        needsToBeSaved = true;
    }

    private static void moveItem(Scanner in)
    {
        if (list.size() < 2)
        {
            System.out.println("Need at least two items to move.");
            return;
        }

        viewList();
        int from = SafeInput.getRangedInt(in, "Enter number of item to move", 1, list.size());
        int to = SafeInput.getRangedInt(in, "Enter new position", 1, list.size());


        int fromIndex = from - 1;
        int toIndex = to - 1;

        String item = list.remove(fromIndex);


        if (toIndex > fromIndex)
        {
            toIndex--;
        }

        list.add(toIndex, item);
        needsToBeSaved = true;
    }

    private static void clearList(Scanner in)
    {
        if (list.isEmpty())
        {
            System.out.println("List is already empty.");
            return;
        }

        boolean confirm = SafeInput.getYNConfirm(in, "Are you sure you want to clear the entire list");
        if (confirm)
        {
            list.clear();
            needsToBeSaved = true;
        }
    }




    private static void handleOpen(Scanner in) throws IOException
    {

        if (needsToBeSaved && !list.isEmpty())
        {
            boolean saveFirst = SafeInput.getYNConfirm(in,
                    "You have unsaved changes. Save current list before loading a new one");
            if (saveFirst)
            {
                handleSave(in);
            }
        }

        String baseName = SafeInput.getNonZeroLenString(in,
                "Enter base filename to open (without .txt)");
        String fileName = baseName + ".txt";

        Path filePath = Paths.get("src", fileName);

        list = loadFile(filePath);   // may throw IOException
        currentFileName = fileName;
        needsToBeSaved = false;

        System.out.println("File loaded: " + filePath.toAbsolutePath());
        viewList();
    }


    private static void handleSave(Scanner in) throws IOException
    {
        if (list.isEmpty())
        {
            System.out.println("List is empty. Nothing to save.");
            return;
        }

        if (currentFileName == null)
        {
            String baseName = SafeInput.getNonZeroLenString(in,
                    "Enter base filename to save as (without .txt)");
            currentFileName = baseName + ".txt";
        }

        Path filePath = Paths.get("src", currentFileName);
        saveFile(filePath, list);

        needsToBeSaved = false;
        System.out.println("List saved to: " + filePath.toAbsolutePath());
    }

    private static boolean handleQuit(Scanner in)
    {
        if (needsToBeSaved && !list.isEmpty())
        {
            boolean save = SafeInput.getYNConfirm(in,
                    "You have unsaved changes. Do you want to save before quitting");
            if (save)
            {
                try
                {
                    handleSave(in);
                }
                catch (IOException e)
                {
                    System.out.println("Error saving file: " + e.getMessage());
                }
            }
        }

        return true;  // end main loop
    }


    private static void saveFile(Path filePath, ArrayList<String> data)
            throws IOException
    {
        OutputStream out =
                new BufferedOutputStream(
                        Files.newOutputStream(filePath,
                                StandardOpenOption.CREATE,
                                StandardOpenOption.TRUNCATE_EXISTING));

        BufferedWriter writer =
                new BufferedWriter(new OutputStreamWriter(out));

        for (String rec : data)
        {
            writer.write(rec, 0, rec.length());
            writer.newLine();
        }

        writer.close();
    }


    private static ArrayList<String> loadFile(Path filePath)
            throws IOException, FileNotFoundException
    {
        ArrayList<String> result = new ArrayList<>();

        InputStream in =
                Files.newInputStream(filePath, StandardOpenOption.READ);
        BufferedReader reader =
                new BufferedReader(new InputStreamReader(in));

        String rec;
        while ((rec = reader.readLine()) != null)
        {
            result.add(rec);
        }

        reader.close();
        return result;
    }
}