package modules.duplicates

import com.google.common.io.Files
import java.io.File
import java.nio.charset.Charset

// Avoid duplicates found
//# happen because the same block of text matches, but probably with an empty line in front or behind

data class DuplicateCandidateLocation(
    val filename: String,
    val file_start_line: Int,
    val file_end_line: Int
)

data class DuplicateBlockCandidate(
    val occurrences_count: Int,
    val lines_of_code: Int,
    val locations: List<DuplicateCandidateLocation>
)

class DuplicateCodeDetector() {
    fun get_duplicate_code_percententage(files: List<String>, loc: Int) {
        val min_line_matches = 4
        val min_character_matches = 30

        val block_dict = mutableMapOf<String, DuplicateBlockCandidate>()

        val filesData = mutableMapOf<String, List<String>>()


        // read Kotlin and Java files
        files.forEach { filePath ->
            if (filePath.endsWith(".kt") || filePath.endsWith(".java")) {
                filesData[filePath] = Files.readLines(File(filePath), Charset.defaultCharset())
            }
        }

        println("Starting with min length: $min_line_matches")

        // do the initial cycle only once
        // that gives a set of candidates with length min_line_matches
        // we take one candidates
        // we take the locations/files this candidates has appeared in
        // for each location, we grow the lines by 1
        // if this results the same number of candidates as there already was, then we remove those previous candidates

        filesData.forEach { fileName, lines ->
            val lines_to_scan = min_line_matches

            for (line_idx in 0..(lines.size - min_line_matches)) {
                if (line_idx + lines_to_scan >= lines.size) {
                    break
                }

                if (!_is_legal_start_or_end_line_for_block(lines[line_idx])) {
                    continue
                }

                if (!_is_legal_start_or_end_line_for_block(lines[line_idx + lines_to_scan])) {
                    continue
                }

                val hashed_block_string = _create_hashed_string_from_block(
                    lines.subList(
                        line_idx,
                        line_idx + lines_to_scan
                    )
                )

                if (!block_dict.containsKey(hashed_block_string)) {
                    block_dict.put(
                        hashed_block_string, DuplicateBlockCandidate(
                            occurrences_count = 0,
                            lines_of_code = lines_to_scan,
                            locations = emptyList()
                        )
                    )
                }

                val current_entry = block_dict[hashed_block_string]!!
                block_dict[hashed_block_string] = current_entry.copy(
                    occurrences_count = current_entry.occurrences_count + 1,
                    locations = current_entry.locations + DuplicateCandidateLocation(
                        filename = fileName,
                        file_start_line = line_idx,
                        file_end_line = line_idx + lines_to_scan
                    )
                )
            }
        }

        println("Done first step, found ${block_dict.size} candidates with min number of lines ${min_line_matches}")

        // to continue here, the first step seems to work ok, but below just seems to keep running forever, so debug
        val already_expanded_dict = mutableMapOf<String, Int>()

        var extra_lines = 1

        while (true) {
            val entries_to_explore =
                block_dict.keys.filter { !already_expanded_dict.containsKey(it) }.toList()
            val delete_entry_list = mutableListOf<String>()
            var found_new = false

            val lines_to_scan = min_line_matches + extra_lines

            for (entry in entries_to_explore) {

                if (block_dict[entry]!!.occurrences_count < 2) {
                    continue
                }

                var can_delete_entry = false

                for (location in block_dict[entry]!!.locations) {
                    val lines = filesData[location.filename]!!

                    if (location.file_start_line + lines_to_scan >= lines.size) {
                        break
                    }

                    if (!_is_legal_start_or_end_line_for_block(lines[location.file_start_line])) {
                        continue
                    }

                    if (!_is_legal_start_or_end_line_for_block(lines[location.file_start_line + lines_to_scan])) {
                        continue
                    }

                    val hashed_block_string = _create_hashed_string_from_block(
                        lines.subList(
                            location.file_start_line, location.file_start_line + lines_to_scan
                        )
                    )

                    if (hashed_block_string == entry) {
                        println(hashed_block_string)
                        println("This is problematic")
                        throw IllegalStateException()
                    }

                    if (!block_dict.contains(hashed_block_string)) {
                        block_dict[hashed_block_string] = DuplicateBlockCandidate(
                            occurrences_count = 0,
                            lines_of_code = lines_to_scan,
                            locations = emptyList()
                        )
                    }

                    val current_entry = block_dict[hashed_block_string]!!
                    block_dict[hashed_block_string] = current_entry.copy(
                        occurrences_count = current_entry.occurrences_count + 1,
                        locations = current_entry.locations + DuplicateCandidateLocation(
                            filename = location.filename,
                            file_start_line = location.file_start_line,
                            file_end_line = location.file_start_line + lines_to_scan
                        )
                    )
                    if (block_dict[hashed_block_string]!!.occurrences_count > 1) {
                        found_new = true
                    }
                    if (block_dict[hashed_block_string]!!.occurrences_count == block_dict[entry]!!.occurrences_count) {
                        can_delete_entry = true
                    }
                }
                if (can_delete_entry) {
                    delete_entry_list.add(entry)
                }
                already_expanded_dict[entry] = 1
            }
            if (!found_new) {
                break
            }
            extra_lines += 1
            delete_entry_list.forEach { block_dict.remove(it) }
        }

        val filtered_dict =
            block_dict.filter { it.key.length > min_character_matches && it.value.occurrences_count > 1 }

        println("Number of duplicate blocks found: ${filtered_dict.size}")
        val lines = mutableMapOf<String, Int>()
        filtered_dict.forEach { entry ->
            entry.value.locations.forEach { location ->
                for (i in location.file_start_line..location.file_end_line) {
                    lines["${location.filename}${i}"] = 1
                }
            }
        }
        try {
            println("${lines.size}")
            println("${lines.size.toFloat() / loc.toFloat()}")
        } catch (e : ArithmeticException) {
            println("Zero division exception")
        }
    }

    fun _is_legal_start_or_end_line_for_block(line: String): Boolean {
        var processed_line = line
        processed_line = processed_line.replace("{", "")
        processed_line = processed_line.replace("}", "")
        processed_line = processed_line.trim()

        if (processed_line.startsWith("//")) {
            return false
        }

        if (processed_line.startsWith("import")) {
            return false
        }

        if (processed_line.trim() == "") {
            return false
        }

        return true
    }

    fun _create_hashed_string_from_block(lines: List<String>): String {
        return lines.map { it.trim() }.joinToString(separator = "\n")
    }
}

/*
class DuplicateCodeDetector:

    def get_duplicate_code_percentage(self, app_path, loc):

        candidates = []

        # do the initial cycle only once
        # that gives a set of candidates with length min_line_matches
        # we take one candidates
        # we take the locations/files this candidates has appeared in
        # for each location, we grow the lines by 1
        # if this results the same number of candidates as there already was, then we remove those previous candidates
        #

        print("Starting with min length:", min_line_matches, len(candidates))

        for filename in files_data:

            lines = files_data[filename]

            lines_to_scan = min_line_matches

            for line_idx in range(0, len(lines) - (min_line_matches)):

                if line_idx + lines_to_scan >= len(lines):
                    break

                if not self._is_legal_start_or_end_line_for_block(lines[line_idx]):
                    continue

                if not self._is_legal_start_or_end_line_for_block(lines[line_idx + lines_to_scan]):
                    continue

                hashed_block_string = self._create_hashed_string_from_block(
                    lines[line_idx:line_idx + lines_to_scan])

                if hashed_block_string not in block_dict:
                    block_dict[hashed_block_string] = DuplicateBlockCandidate(
                        occurrences_count=0,
                        lines_of_code=lines_to_scan,
                        locations=[]
                    )

                current_entry = block_dict[hashed_block_string]
                current_entry.occurrences_count += 1
                current_entry.locations.append(DuplicateCandidateLocation(
                    filename=filename,
                    file_start_line=line_idx,
                    file_end_line=line_idx + lines_to_scan
                ))
                block_dict[hashed_block_string] = current_entry

        print (f"Done first step, found {len(block_dict)} candidates with min number of lines {min_line_matches}")

        # to continue here, the first step seems to work ok, but below just seems to keep running forever, so debug
        already_expanded_dict = {}

        extra_lines = 1
        while True:

            entries_to_explore = [x[0] for x in block_dict.items() if x[0] not in already_expanded_dict]
            delete_entry_list = []
            found_new = False

            lines_to_scan = min_line_matches + extra_lines
            # print("Scanning", lines_to_scan, "lines")

            for entry in entries_to_explore:
                if block_dict[entry].occurrences_count < 2:
                    continue
                # print ("")
                # print("Entry with", len(block_dict[entry].locations), "locations")
                # print (entry)
                can_delete_entry = False


                for location in block_dict[entry].locations:
                    lines = files_data[location.filename]

                    if location.file_start_line + lines_to_scan >= len(lines):
                        break

                    if not self._is_legal_start_or_end_line_for_block(lines[location.file_start_line]):
                        continue

                    if not self._is_legal_start_or_end_line_for_block(lines[location.file_start_line + lines_to_scan]):
                        continue

                    # print("Location", location.filename, location.file_start_line,
                    #       location.file_start_line + lines_to_scan, "previous", location.file_start_line, location.file_end_line)
                    hashed_block_string = self._create_hashed_string_from_block(
                        lines[location.file_start_line:location.file_start_line + lines_to_scan])

                    if hashed_block_string == entry:
                        print (hashed_block_string)
                        print("This is problematic")
                        input('')


                    if hashed_block_string not in block_dict:
                        block_dict[hashed_block_string] = DuplicateBlockCandidate(
                            occurrences_count=0,
                            lines_of_code=lines_to_scan,
                            locations=[]
                        )

                    current_entry = block_dict[hashed_block_string]
                    current_entry.occurrences_count += 1
                    current_entry.locations.append(DuplicateCandidateLocation(
                        filename=location.filename,
                        file_start_line=location.file_start_line,
                        file_end_line=location.file_start_line + lines_to_scan
                    ))
                    block_dict[hashed_block_string] = current_entry
                    if current_entry.occurrences_count > 1:
                        found_new = True
                    if current_entry.occurrences_count == block_dict[entry].occurrences_count:
                        can_delete_entry = True

                if can_delete_entry:
                    delete_entry_list.append(entry)

                already_expanded_dict[entry] = 1
                # input('')

            if not found_new:
                break

            extra_lines += 1

            # delete entries that have an expansion with the same occurrences count, no use to keep the original
            for entry in delete_entry_list:
                del block_dict[entry]


        filtered_dict = {k: v for k, v in block_dict.items() if
                         len(k) > min_character_matches and v.occurrences_count > 1}
        print("Number of duplicate blocks found:", len(filtered_dict))

        lines = {}
        for entry in filtered_dict:
            for location in filtered_dict[entry].locations:
                # self.filename = filename
                # self.file_start_line = file_start_line
                # self.file_end_line = file_end_line
                for i in range(location.file_start_line, location.file_end_line):
                    lines[(location.filename, i)] = 1

        try:
            print(len(lines))
            print(len(lines) / float(loc))

            return float(len(lines)) / float(loc)
        except ZeroDivisionError:
            return 0.0

        #
        #
        # for entry in filtered_dict:
        #     print ("Candidate:", filtered_dict[entry].occurrences_count, "occerrences", filtered_dict[entry].lines_of_code, "lines of code")
        #
        #     print ("Locations:")
        #     for location in filtered_dict[entry].locations:
        #         print (location.filename, location.file_start_line, location.file_end_line)
        #
        #     print("")

 */