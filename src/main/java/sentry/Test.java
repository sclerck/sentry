/**
 * $Header:
 * $Id:
 * $Name:
 */
package sentry;

import io.vertx.core.Vertx;
import io.vertx.core.http.HttpClient;
import io.vertx.core.http.HttpClientRequest;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

/**
 * Purpose:
 * 
 * @author Simon
 * @date 24 Nov 2017
 *
 */
public class Test {
	private static final String ACCESS_TOKEN = "73d839c17dd146559e3e5183ce1d1396eedf64ae2a3844c29a55d2321a16bd11";

	public static void main(String[] args) {
		Vertx vertx = Vertx.vertx();

		HttpClient client = vertx.createHttpClient();

		// listProjectsForOrganizations(client);

		createOrganization(client, "Test", "1234");

	}

	private static void createOrganization(HttpClient client, String name, String id) {
		HttpClientRequest request = client.post(9000, "localhost", "/api/0/organizations/");
		request.putHeader("Authorization", "Bearer " + ACCESS_TOKEN);

		JsonObject params = new JsonObject();
		params.put("name", "test");
		params.put("slug", "2342");

		request.putHeader("Content-Length", Integer.toString(params.toString().length()));

		request.handler(response -> {

		});

		request.write(params.toString());

		request.end();
	}

	private static void listProjectsForOrganizations(HttpClient client) {

		HttpClientRequest request = client.get(9000, "localhost", "/api/0/organizations/");
		request.putHeader("Authorization", "Bearer " + ACCESS_TOKEN);

		request.handler(response -> {
			response.bodyHandler(buffer -> {
				JsonArray organizations = buffer.toJsonArray();

				for (Object organizationO : organizations) {
					JsonObject organization = (JsonObject) organizationO;

					System.out.println("Iterating organisation " + organization.getString("name"));

					String slug = organization.getString("slug");

					HttpClientRequest orgRequest = client.get(9000, "localhost", "/api/0/organizations/" + slug + "/");
					orgRequest.putHeader("Authorization", "Bearer " + ACCESS_TOKEN);

					orgRequest.handler(orgResponse -> {
						orgResponse.bodyHandler(orgBuffer -> {
							JsonObject organization2 = orgBuffer.toJsonObject();

							JsonArray teams = organization2.getJsonArray("teams");

							for (Object teamO : teams) {
								JsonObject team = (JsonObject) teamO;

								System.out.println("Retrieving projects for team " + team.getString("name"));

								JsonArray projects = team.getJsonArray("projects");

								for (Object projectO : projects) {
									JsonObject project = (JsonObject) projectO;
									System.out.println("Found project: " + project.getString("name"));
								}
							}
						});
					});

					orgRequest.end();

				}
			});
		});

		request.end();
	}
}
